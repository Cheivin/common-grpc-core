package top.cheivin.grpc.exception;

/**
 * channel异常
 */
public class ChannelException extends InvokeException {
    public ChannelException(String message) {
        super(message);
    }

    public ChannelException(String message, Throwable cause) {
        super(message, cause);
    }
}
