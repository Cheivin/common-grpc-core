package top.cheivin.grpc.exception;

import top.cheivin.grpc.core.RemoteInstance;

/**
 * channel异常
 */
public class ChannelException extends InvokeException {
    public ChannelException(String message) {
        super(message);
    }

    public ChannelException(String message, RemoteInstance remoteInstance) {
        super(message, remoteInstance, null);
    }
}
