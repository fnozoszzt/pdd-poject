package fnozoszzt.pdd.session;


public class StoreKeyGenerator {
    private final String namespace;

    public StoreKeyGenerator(String namespace) {
        this.namespace = namespace;
    }

    public String generate(String name) {
        return "gs_" + this.namespace + "_" + name;
    }
}
