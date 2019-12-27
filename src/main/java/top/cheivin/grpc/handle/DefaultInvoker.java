package top.cheivin.grpc.handle;

import top.cheivin.grpc.core.GrpcRequest;
import top.cheivin.grpc.core.GrpcResponse;
import top.cheivin.grpc.util.ProtoBufUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 默认反射调用类，仅支持JAVA
 */
public class DefaultInvoker implements Invoker {

    @Override
    public String getDataFormat() {
        return "BYTES";
    }

    @Override
    public GrpcRequest parseRequest(byte[] request) {
        return ProtoBufUtils.deserialize(request, GrpcRequest.class);
    }

    @Override
    public byte[] packResponse(GrpcResponse response) {
        return ProtoBufUtils.serialize(response);
    }

    @Override
    public GrpcResponse invoke(Object instance, GrpcRequest request) {
        try {
            // 获取参数类型并调用方法
            Class<?>[] argTypes = ProtoBufUtils.getObjTypes(request.getArgs());
            Method method = instance.getClass().getMethod(request.getMethodName(), argTypes);
            return GrpcResponse.Status.success(method.invoke(instance, request.getArgs()));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return GrpcResponse.Status.error(e.getMessage());
        }
    }
}
