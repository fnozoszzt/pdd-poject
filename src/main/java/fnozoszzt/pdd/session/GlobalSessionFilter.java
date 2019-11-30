package fnozoszzt.pdd.session;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

@WebFilter(filterName="GlobalSessionFilter",urlPatterns="/*")
@Order(1)
public class GlobalSessionFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(GlobalSessionFilter.class);
    public static final String DEFAULT_GLOBAL_NAMESPACE = "GLOBAL";
    public static final String DEFAULT_SESSION_ID_NAME = "__gsid__";
    public static final String DEFAULT_PATH = "/";
    public static final int DEFAULT_TIMEOUT = 30;
    public static final String DEFAULT_EXCLUDE = "/.+\\.(html|jpg|jpeg|png|gif|js|css|swf)";
    private boolean enable = true;
    protected SessionStore sessionStore;
    protected GlobalSessionFilterSettings settings = new GlobalSessionFilterSettings();

    public GlobalSessionFilter() {
        this.settings.setNamespace("GLOBAL");
        this.settings.setExcludeRegExp("/.+\\.(html|jpg|jpeg|png|gif|js|css|swf)");
        this.settings.setSessionIdKey("__gsid__");
        this.settings.setPath("/");
        this.settings.setSecure(Boolean.FALSE);
        this.settings.setHttpOnly(Boolean.FALSE);
        this.settings.setSessionTimeout(30);
    }

    protected Cookie getCurrentValidSessionIdCookie(HttpServletRequest req) {
        if (req.getCookies() != null) {
            Cookie[] var5;
            int var4 = (var5 = req.getCookies()).length;

            for(int var3 = 0; var3 < var4; ++var3) {
                Cookie cookie = var5[var3];
                if (cookie.getName().equalsIgnoreCase(this.settings.getSessionIdKey()) && cookie.getValue() != null && cookie.getValue().trim().length() > 0) {
                    if (log.isDebugEnabled()) {
                        log.debug("SessionId cookie is found. (" + this.settings.getSessionIdKey() + " -> " + cookie.getValue() + ")");
                    }

                    return cookie;
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("SessionId cookie is not found.");
        }

        return null;
    }

    protected Cookie generateSessionIdCookie(String sessionIdValue) {
        Cookie sessionIdCookie = new Cookie(this.settings.getSessionIdKey(), sessionIdValue);
        if (this.settings.getDomain() != null) {
            sessionIdCookie.setDomain(this.settings.getDomain());
        }

        if (this.settings.getPath() != null) {
            sessionIdCookie.setPath(this.settings.getPath());
        } else {
            sessionIdCookie.setPath("/");
        }

        sessionIdCookie.setSecure(this.settings.isSecure());
        if (this.settings.getMaxAge() > -1) {
            sessionIdCookie.setMaxAge(this.settings.getMaxAge());
        }

        return sessionIdCookie;
    }

    protected GlobalSessionHttpRequest createGlobalSessionRequest(HttpServletRequest req, String sessionIdValue) {
        return new GlobalSessionHttpRequest(req, sessionIdValue, this.settings.getNamespace(), this.settings.getSessionTimeout(), this.sessionStore);
    }

    public void init(FilterConfig config) throws ServletException {
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest _req = (HttpServletRequest)req;
        HttpServletResponse _res = (HttpServletResponse)res;
        if (!this.enable) {
            chain.doFilter(_req, _res);
        } else {
            if (isGlobalSessionHttpRequest(_req)) {
                if (log.isDebugEnabled()) {
                    log.debug("GlobalSessionHttpRequest is already applied.");
                }

                chain.doFilter(_req, _res);
            } else if (this.settings.getExcludeRegExp() != null && _req.getRequestURI().matches(this.settings.getExcludeRegExp())) {
                if (log.isDebugEnabled()) {
                    log.debug("This URI is excluded. (URI: " + _req.getRequestURI() + ")");
                }

                chain.doFilter(_req, _res);
            } else {
                Cookie currentValidSessionIdCookie = this.getCurrentValidSessionIdCookie(_req);
                String sessionIdValue = null;
                if (currentValidSessionIdCookie == null) {
                    sessionIdValue = UUID.randomUUID().toString().replace("-", "").toUpperCase();
                } else {
                    sessionIdValue = currentValidSessionIdCookie.getValue();
                }

                GlobalSessionHttpRequest _wrappedReq = this.createGlobalSessionRequest(_req, sessionIdValue);
                if (!_wrappedReq.getSession().isValid()) {
                    sessionIdValue = UUID.randomUUID().toString().replace("-", "").toUpperCase();
                    currentValidSessionIdCookie = null;
                    _wrappedReq.changeSessionId(sessionIdValue);
                }

                if (currentValidSessionIdCookie == null) {
                    Cookie newSessionIdCookie = this.generateSessionIdCookie(sessionIdValue);
                    String setCookie = CookieUtil.createSetCookieHeaderValue(newSessionIdCookie, this.settings.isHttpOnly());
                    _res.addHeader("Set-Cookie", setCookie);
                    setSessionStatus(_req, GlobalSessionFilter.SessionStatus.fixed);
                    if (log.isDebugEnabled()) {
                        log.debug("SessionId cookie is updated. (" + sessionIdValue + ")");
                    }
                }

                chain.doFilter(_wrappedReq, _res);
            }

        }
    }

    public void destroy() {
        if (this.sessionStore != null) {
            this.sessionStore.shutdown();
        }

    }

    protected static String getConfigValue(FilterConfig config, String keyName) {
        String fromInitParam = config.getInitParameter(keyName);
        return fromInitParam != null ? fromInitParam : System.getProperty(keyName);
    }

    protected static String getConfigValue(FilterConfig config, String keyName, String defaultVal) {
        String fromInitParam = config.getInitParameter(keyName);
        if (fromInitParam != null) {
            return fromInitParam;
        } else {
            fromInitParam = System.getProperty(keyName);
            return fromInitParam != null ? fromInitParam : defaultVal;
        }
    }

    protected static void setSessionStatus(HttpServletRequest req, GlobalSessionFilter.SessionStatus status) {
        req.setAttribute("__sessionStatus__", status);
    }

    protected static GlobalSessionFilter.SessionStatus getSessionStatus(HttpServletRequest req) {
        Object status = req.getAttribute("__sessionStatus__");
        return status == null ? GlobalSessionFilter.SessionStatus.unknown : (GlobalSessionFilter.SessionStatus)status;
    }

    protected static boolean isValidSession(GlobalSessionHttpRequest req) {
        return getSessionStatus(req) == GlobalSessionFilter.SessionStatus.fixed ? true : req.getSession().isValid();
    }

    protected static boolean isGlobalSessionHttpRequest(HttpServletRequest req) {
        return req.getSession() instanceof GlobalHttpSession;
    }

    public void setSessionStore(SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    public void setSessionId(String sessionId) {
        this.settings.setSessionIdKey(sessionId);
    }

    public void setNamespace(String namespace) {
        this.settings.setNamespace(namespace);
    }

    public void setDomain(String domain) {
        this.settings.setDomain(domain);
    }

    public void setMaxAge(int maxAge) {
        this.settings.setMaxAge(maxAge);
    }

    public void setPath(String path) {
        this.settings.setPath(path);
    }

    public void setSecure(boolean secure) {
        this.settings.setSecure(secure);
    }

    public void setHttpOnly(boolean httpOnly) {
        this.settings.setHttpOnly(httpOnly);
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.settings.setSessionTimeout(sessionTimeout);
    }

    public void setExcludeRegExp(String excludeRegExp) {
        this.settings.setExcludeRegExp(excludeRegExp);
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public static class RequestAttributeKey {
        protected static final String SESSION_STATUS = "__sessionStatus__";

        public RequestAttributeKey() {
        }
    }

    static enum SessionStatus {
        unknown,
        fixed;

        private SessionStatus() {
        }
    }
}
