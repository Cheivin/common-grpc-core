package top.cheivin.grpc.exception;

/**
 * 调用异常
 */
public class InvokeException extends Exception {
    public InvokeException(String message) {
        super(message);
    }

    public InvokeException(String message, Throwable cause) {
        super(message, cause);
    }
}
