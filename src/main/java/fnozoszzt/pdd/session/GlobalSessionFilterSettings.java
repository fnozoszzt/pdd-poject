package fnozoszzt.pdd.session;

public class GlobalSessionFilterSettings {
    private String namespace;
    private String excludeRegExp;
    private String sessionIdKey;
    private String domain;
    private String path;
    private boolean secure;
    private boolean httpOnly;
    private int maxAge = -1;
    private Integer sessionTimeout;

    public GlobalSessionFilterSettings() {
    }

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getExcludeRegExp() {
        return this.excludeRegExp;
    }

    public void setExcludeRegExp(String excludeRegExp) {
        this.excludeRegExp = excludeRegExp;
    }

    public String getSessionIdKey() {
        return this.sessionIdKey;
    }

    public void setSessionIdKey(String sessionIdKey) {
        this.sessionIdKey = sessionIdKey;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSecure() {
        return this.secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure != null && secure;
    }

    public Integer getSessionTimeout() {
        return this.sessionTimeout;
    }

    public void setSessionTimeout(Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public boolean isHttpOnly() {
        return this.httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public int getMaxAge() {
        return this.maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }
}
