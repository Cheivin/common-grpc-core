package top.cheivin.grpc.exception;

/**
 * 实例化异常
 */
public class InstanceException extends Exception {
    public InstanceException(String message) {
        super(message);
    }

    public InstanceException(Throwable cause) {
        super(cause);
    }
}
