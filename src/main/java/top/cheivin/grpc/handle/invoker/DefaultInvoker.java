package top.cheivin.grpc.handle.invoker;

import top.cheivin.grpc.core.GrpcRequest;
import top.cheivin.grpc.core.GrpcResponse;
import top.cheivin.grpc.handle.Invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 默认反射调用类，仅支持JAVA
 */
public class DefaultInvoker implements Invoker {

    @Override
    public GrpcResponse invoke(Object instance, GrpcRequest request) {
        try {
            // 获取参数类型并调用方法
            Class<?>[] argTypes = getObjTypes(request.getArgs());
            Method method = instance.getClass().getMethod(request.getMethodName(), argTypes);
            return GrpcResponse.Status.success(method.invoke(instance, request.getArgs()));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return GrpcResponse.Status.error(e.getMessage());
        }
    }

    private Class<?>[] getObjTypes(Object[] args) {
        if (args == null) {
            return null;
        }
        Class<?>[] types = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            Class<?> type = args[i].getClass();
            types[i] = type;
        }
        return types;
    }
}
