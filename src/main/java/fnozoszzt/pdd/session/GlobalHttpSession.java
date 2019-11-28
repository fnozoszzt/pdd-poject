package fnozoszzt.pdd.session;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalHttpSession implements HttpSession {
    private static final Logger logger = LoggerFactory.getLogger(GlobalHttpSession.class);
    private static final String META_STATE = "state";
    private static final String META_CT = "ct";
    private static final String META_LT = "lt";
    private static final String ATTRIBUTES_KEY = "_attr_%s";
    private static final String METADATA_KEY = "_meta_%s";
    private final String sessionId;
    private final SessionStore store;
    private final HttpSession session;
    private final StoreKeyGenerator keyGenerator;
    private boolean isNewlyCreated = false;
    private int maxInactiveIntervalForCache = 1800;
    private Boolean invalidated = false;
    private Date creationTime;
    private Date lastAccessedTime;

    public StoreKeyGenerator getKeyGenerator() {
        return this.keyGenerator;
    }

    public GlobalHttpSession(String sessionId, SessionStore store, String namespace, Integer timeoutMinutes, HttpSession session, boolean create) {
        if (sessionId != null && sessionId.trim().length() != 0) {
            if (store == null) {
                throw new IllegalArgumentException("store should not be empty.");
            } else if (namespace != null && namespace.trim().length() != 0) {
                if (timeoutMinutes == null) {
                    throw new IllegalArgumentException("timeoutMinutes should not be empty.");
                } else if (session == null) {
                    throw new IllegalArgumentException("session should not be empty.");
                } else {
                    this.sessionId = sessionId;
                    this.store = store;
                    this.session = session;
                    this.keyGenerator = new StoreKeyGenerator(namespace);
                    this.maxInactiveIntervalForCache = timeoutMinutes * 60;
                    if (!(session instanceof GlobalHttpSession)) {
                        session.setMaxInactiveInterval(1);
                    }

                    if (create) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("GlobalHttpSession init create=true (" + sessionId + ")");
                        }

                        this.createSession(sessionId);
                    } else {
                        this.loadMetadateFromStore();
                        if (this.lastAccessedTime == null) {
                            this.createSession(sessionId);
                        } else {
                            if (logger.isDebugEnabled()) {
                                logger.debug("A new GlobalHttpSession is created. (sessionId: " + sessionId + ", lastAccessedTime: " + this.lastAccessedTime + ")");
                            }

                            if (this.isExpired((long)(this.maxInactiveIntervalForCache / 60))) {
                                this.lastAccessedTime = new Date();
                                store.set(this.getId(), this.keyGenerator.generate(String.format("_meta_%s", "lt")), this.lastAccessedTime);
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Update lastAccessedTime (" + sessionId + ", " + this.lastAccessedTime + ")");
                                }
                            }

                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("namespace should not be empty.");
            }
        } else {
            throw new IllegalArgumentException("sessionId should not be empty.");
        }
    }

    private void createSession(String sessionId) {
        this.isNewlyCreated = true;
        this.invalidated = false;
        this.creationTime = new Date();
        this.lastAccessedTime = this.creationTime;
        Map<String, Serializable> meta = new HashMap();
        meta.put(this.keyGenerator.generate(String.format("_meta_%s", "state")), this.invalidated);
        meta.put(this.keyGenerator.generate(String.format("_meta_%s", "ct")), this.creationTime);
        meta.put(this.keyGenerator.generate(String.format("_meta_%s", "lt")), this.lastAccessedTime);
        this.store.set(this.getId(), meta);
        this.store.setExpire(this.getId(), this.getMaxInactiveInterval());
        if (logger.isDebugEnabled()) {
            logger.debug("A totally new GlobalHttpSession is created. (sessionId: " + sessionId + ")");
        }

    }

    private void loadMetadateFromStore() {
        List<String> keys = Arrays.asList(this.keyGenerator.generate(String.format("_meta_%s", "state")), this.keyGenerator.generate(String.format("_meta_%s", "ct")), this.keyGenerator.generate(String.format("_meta_%s", "lt")));
        List<Serializable> values = this.store.get(this.getId(), keys);
        if (values != null && values.size() == keys.size()) {
            this.invalidated = (Boolean)values.get(0);
            this.creationTime = (Date)values.get(1);
            this.lastAccessedTime = (Date)values.get(2);
        }

    }

    private boolean isExpired(long expiredInSec) {
        if (this.lastAccessedTime != null) {
            return this.lastAccessedTime.getTime() + expiredInSec * 1000L < (new Date()).getTime();
        } else {
            return true;
        }
    }

    private Serializable getAttributeValueFromStore(String name) {
        String key = this.keyGenerator.generate(String.format("_attr_%s", name));
        return this.store.get(this.getId(), key);
    }

    private void setAttributeValueToStore(String name, Serializable value) {
        String key = this.keyGenerator.generate(String.format("_attr_%s", name));
        if (value == null) {
            this.store.remove(this.getId(), key);
        } else {
            this.store.set(this.getId(), key, value);
        }

    }

    public boolean isValid() {
        boolean isNotInvalidated = this.invalidated != null && Boolean.FALSE.equals(this.invalidated) && !this.isExpired((long)this.maxInactiveIntervalForCache);
        if (logger.isDebugEnabled()) {
            logger.debug("isValid is called. (isNotInvalidated: " + isNotInvalidated + ")");
        }

        return isNotInvalidated;
    }

    public Object getAttribute(String name) {
        Object value = this.getAttributeValueFromStore(name);
        if (logger.isDebugEnabled()) {
            logger.debug("getAttribute is called. (sessionId: " + this.sessionId + ", " + name + " -> " + value + ")");
        }

        return value;
    }

    public Enumeration<String> getAttributeNames() {
        Set<String> set = this.store.getKeys(this.getId());
        final Iterator<String> names = set.iterator();
        return new Enumeration<String>() {
            public boolean hasMoreElements() {
                return names.hasNext();
            }

            public String nextElement() {
                return (String)names.next();
            }
        };
    }

    public void invalidate() {
        if (logger.isDebugEnabled()) {
            logger.debug("invalidate is called. (sessionId: " + this.sessionId + ")");
        }

        this.invalidated = true;
        this.store.set(this.getId(), this.keyGenerator.generate(String.format("_meta_%s", "state")), this.invalidated);
        this.clearAllAttributes();
    }

    private void clearAllAttributes() {
        Set<String> set = this.store.getKeys(this.getId());
        Iterator var3 = set.iterator();

        while(var3.hasNext()) {
            String name = (String)var3.next();
            this.setAttributeValueToStore(name, (Serializable)null);
        }

    }

    public void removeAttribute(String name) {
        if (logger.isDebugEnabled()) {
            logger.debug("removeAttribute is called. (sessionId: " + this.sessionId + ", " + name + " -> null)");
        }

        this.setAttributeValueToStore(name, (Serializable)null);
    }

    public void setAttribute(String name, Object value) {
        if (logger.isDebugEnabled()) {
            logger.debug("setAttribute is called. (sessionId: " + this.sessionId + ", " + name + " -> " + value + ")");
        }

        if (value == null) {
            this.removeAttribute(name);
        } else if (value instanceof Serializable) {
            this.setAttributeValueToStore(name, (Serializable)value);
        } else {
            String message = "The value should be an instance of java.io.Serializable. (" + value + ")";
            throw new IllegalArgumentException(message);
        }
    }

    public Object getValue(String name) {
        return this.getAttribute(name);
    }

    public String[] getValueNames() {
        Enumeration<String> names = this.getAttributeNames();
        return (String[])Collections.list(names).toArray(new String[0]);
    }

    public String getId() {
        return this.sessionId;
    }

    public long getCreationTime() {
        return this.creationTime != null ? this.creationTime.getTime() : 0L;
    }

    public long getLastAccessedTime() {
        return this.lastAccessedTime != null ? this.lastAccessedTime.getTime() : 0L;
    }

    public int getMaxInactiveInterval() {
        return this.maxInactiveIntervalForCache;
    }

    public ServletContext getServletContext() {
        return this.session.getServletContext();
    }

    public HttpSessionContext getSessionContext() {
        return this.session.getSessionContext();
    }

    public boolean isNew() {
        return this.isNewlyCreated;
    }

    public void putValue(String name, Object value) {
        this.setAttribute(name, value);
    }

    public void removeValue(String name) {
        this.removeAttribute(name);
    }

    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveIntervalForCache = interval;
        this.store.setExpire(this.getId(), this.maxInactiveIntervalForCache);
    }

    public String toString() {
        return "GlobalHttpSession(id: " + this.getId() + ", invalidated: " + this.invalidated + ", creationTime: " + this.getCreationTime() + ", lastAccessedTime: " + this.getLastAccessedTime() + ", maxInactiveInterval: " + this.getMaxInactiveInterval() + ")";
    }
}
