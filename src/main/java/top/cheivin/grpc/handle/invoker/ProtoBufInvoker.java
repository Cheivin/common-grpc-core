package top.cheivin.grpc.handle.invoker;

import top.cheivin.grpc.core.GrpcRequest;
import top.cheivin.grpc.core.GrpcResponse;
import top.cheivin.grpc.handle.Invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * protobuf序列化方式参数 反射调用类，仅支持JAVA
 */
public class ProtoBufInvoker implements Invoker {

    @Override
    public GrpcResponse invoke(Object instance, GrpcRequest request) {
        try {
            // 无参时
            if (request.getArgs() == null || request.getArgs().length == 0) {
                Method method = instance.getClass().getMethod(request.getMethodName());
                return GrpcResponse.Status.success(method.invoke(instance));
            }
            // 有参时，取参数类型
            Class<?>[] types = new Class[request.getArgs().length];
            for (int i = 0; i < request.getArgs().length; i++) {
                Class<?> type = request.getArgs()[i].getClass();
                types[i] = type;
            }
            // 再寻找方法
            Method method = instance.getClass().getMethod(request.getMethodName(), types);
            // 调用并返回
            return GrpcResponse.Status.success(method.invoke(instance, request.getArgs()));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return GrpcResponse.Status.error(e.getMessage());
        }
    }

}
