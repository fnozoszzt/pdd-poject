package fnozoszzt.pdd.session;


import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class GlobalSessionHttpRequest extends HttpServletRequestWrapper {
    private String sessionId;
    private final String namespace;
    private int timeoutMinutes;
    private final SessionStore store;
    private GlobalHttpSession session;

    public GlobalSessionHttpRequest(ServletRequest request, String sessionId, String namespace, Integer timeoutMinutes, SessionStore store) {
        super((HttpServletRequest)request);
        this.sessionId = sessionId;
        this.namespace = namespace;
        this.timeoutMinutes = timeoutMinutes;
        this.store = store;
        this.session = new GlobalHttpSession(sessionId, store, namespace, timeoutMinutes, super.getSession(), false);
    }

    public GlobalHttpSession getSession() {
        return this.session;
    }

    public GlobalHttpSession getSession(boolean create) {
        if (create && (this.getSession() == null || !this.getSession().isValid())) {
            this.session = new GlobalHttpSession(this.sessionId, this.store, this.namespace, this.timeoutMinutes, super.getSession(), create);
        }

        return this.session;
    }

    public synchronized void changeSessionId(String sessionId) {
        this.sessionId = sessionId;
        this.session = new GlobalHttpSession(sessionId, this.store, this.namespace, this.timeoutMinutes, super.getSession(), true);
    }
}
