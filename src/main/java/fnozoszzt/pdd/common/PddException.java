package fnozoszzt.pdd.common;

public class PddException extends Exception {

    private Integer errorCode;
    private Object result;

    public PddException() {
    }

    public PddException(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public PddException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public PddException(Integer errorCode, String message, Object result){
        super(message);
        this.errorCode = errorCode;
        this.result = result;
    }

    public PddException(Integer errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public Object getResult() {
        return result;
    }
}
