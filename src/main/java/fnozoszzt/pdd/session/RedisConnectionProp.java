package fnozoszzt.pdd.session;


import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;

public class RedisConnectionProp {
    private String host;
    private int port;
    private String password;

    public RedisConnectionProp() {
    }

    public static RedisConnectionProp newConfig(String redisUrl) {
        String[] parts = redisUrl.split("\\?");
        String[] hostport = parts[0].split(":");
        Preconditions.checkArgument(hostport.length == 2, "invalid redisUrl");
        String host = hostport[0];
        int port = Integer.valueOf(hostport[1]);
        Map<String, String> paramMap = new HashMap();
        if (parts.length > 1) {
            String[] params = parts[1].split("&");
            String[] var10 = params;
            int var9 = params.length;

            for(int var8 = 0; var8 < var9; ++var8) {
                String param = var10[var8];
                String[] pairs = param.split("=");
                if (pairs.length == 2) {
                    paramMap.put(pairs[0], pairs[1]);
                }
            }
        }

        String password = (String)paramMap.get("password");
        RedisConnectionProp config = new RedisConnectionProp();
        config.setHost(host);
        config.setPort(port);
        config.setPassword(password);
        return config;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
