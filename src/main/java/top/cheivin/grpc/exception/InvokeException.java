package top.cheivin.grpc.exception;

import top.cheivin.grpc.core.GrpcRequest;
import top.cheivin.grpc.core.RemoteInstance;

/**
 * 调用异常
 */
public class InvokeException extends Exception {
    private RemoteInstance remoteInstance;
    private GrpcRequest grpcRequest;

    public RemoteInstance getRemoteInstance() {
        return remoteInstance;
    }

    public GrpcRequest getGrpcRequest() {
        return grpcRequest;
    }

    public InvokeException(String message) {
        super(message);
    }

    public InvokeException(String message, RemoteInstance remoteInstance, GrpcRequest grpcRequest) {
        super(message);
        this.remoteInstance = remoteInstance;
        this.grpcRequest = grpcRequest;
    }

    public InvokeException(String message, Throwable cause, RemoteInstance remoteInstance, GrpcRequest grpcRequest) {
        super(message, cause);
        this.remoteInstance = remoteInstance;
        this.grpcRequest = grpcRequest;
    }

    public InvokeException(String message, Throwable cause) {
        super(message, cause);
    }
}
