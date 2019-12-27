package top.cheivin.grpc.handle;

import top.cheivin.grpc.core.GrpcRequest;
import top.cheivin.grpc.core.GrpcResponse;

/**
 *
 */
public interface Invoker {

    /**
     * 数据序列化方式
     *
     * @return 大写英文字符
     */
    String getDataFormat();

    /**
     * 从字节数组反序列化grpc请求信息
     *
     * @param request bytes
     * @return 请求封装类
     */
    GrpcRequest parseRequest(byte[] request);

    /**
     * 序列化调用结果
     *
     * @param response 调用结果
     * @return bytes
     */
    byte[] packResponse(GrpcResponse response);

    /**
     * 执行本地调用
     *
     * @param obj     对象实例
     * @param request 请求信息
     * @return 执行结果
     */
    GrpcResponse invoke(Object obj, GrpcRequest request);
}
