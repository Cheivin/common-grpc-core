package top.cheivin.grpc.handle;

import top.cheivin.grpc.core.GrpcRequest;
import top.cheivin.grpc.core.GrpcResponse;
import top.cheivin.grpc.core.RemoteInstance;
import top.cheivin.grpc.exception.InvokeException;

/**
 *
 */
public interface Caller {
    GrpcResponse call(RemoteInstance remoteInstance, GrpcRequest grpcRequest) throws InvokeException;
}
