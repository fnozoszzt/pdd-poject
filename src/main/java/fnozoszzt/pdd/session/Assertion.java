package fnozoszzt.pdd.session;

public class Assertion {
    private Assertion() {
    }

    public static void notNullValue(String name, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Null is not allowed for " + name);
        }
    }
}
