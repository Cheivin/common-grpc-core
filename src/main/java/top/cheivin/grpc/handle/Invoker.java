package top.cheivin.grpc.handle;

import top.cheivin.grpc.core.GrpcRequest;
import top.cheivin.grpc.core.GrpcResponse;

/**
 *
 */
public interface Invoker {

    /**
     * 执行本地调用
     *
     * @param obj     对象实例
     * @param request 请求信息
     * @return 执行结果
     */
    GrpcResponse invoke(Object obj, GrpcRequest request);
}
