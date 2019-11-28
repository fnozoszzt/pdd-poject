package fnozoszzt.pdd.session;


import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SessionStore {
    void shutdown();

    <V extends Serializable> V get(String var1, String var2);

    <V extends Serializable> List<V> get(String var1, List<String> var2);

    void remove(String var1, String var2);

    <V extends Serializable> void set(String var1, String var2, V var3);

    <V extends Serializable> void set(String var1, Map<String, V> var2);

    void setExpire(String var1, int var2);

    Set<String> getKeys(String var1);
}
