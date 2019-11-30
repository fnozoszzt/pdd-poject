package fnozoszzt.pdd.bean;

import java.util.Date;

public class User {
    private String owner_name;
    private String access_token;
    private String refresh_token;
    private String owner_id;
    private String str;
    private long login_timestamp;

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public long getLogin_timestamp() {
        return login_timestamp;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setLogin_timestamp(long login_timestamp) {
        this.login_timestamp = login_timestamp;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return "user : " + owner_name;
    }
}
