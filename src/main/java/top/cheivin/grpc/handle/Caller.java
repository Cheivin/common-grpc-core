package top.cheivin.grpc.handle;

import top.cheivin.grpc.core.GrpcRequest;
import top.cheivin.grpc.core.GrpcResponse;
import top.cheivin.grpc.core.RemoteInstance;
import top.cheivin.grpc.exception.InvokeException;

/**
 *
 */
public interface Caller {
    String LANG = "JAVA";

    /**
     * 调用远程服务
     *
     * @param remoteInstance 远程实例信息
     * @param grpcRequest    请求信息
     * @return 请求结果封装
     * @throws InvokeException 调用失败异常
     */
    GrpcResponse call(RemoteInstance remoteInstance, GrpcRequest grpcRequest) throws InvokeException;
}
