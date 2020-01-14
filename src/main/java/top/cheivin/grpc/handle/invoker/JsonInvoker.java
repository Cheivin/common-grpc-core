package top.cheivin.grpc.handle.invoker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import top.cheivin.grpc.core.GrpcRequest;
import top.cheivin.grpc.core.GrpcResponse;
import top.cheivin.grpc.handle.Invoker;
import top.cheivin.grpc.util.gson.DateTypeAdapter;
import top.cheivin.grpc.util.gson.LocalDateTimeTypeAdapter;
import top.cheivin.grpc.util.gson.TimestampTypeAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * json序列化方式参数调用类
 *
 * @author cheivin
 * @date 2020/1/14
 */
public class JsonInvoker implements Invoker {
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateTypeAdapter())
            .registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();
    private static Map<String, MethodInfo> map = new ConcurrentHashMap<>();

    @Override
    public GrpcResponse invoke(Object instance, GrpcRequest request) {
        try {
            Class<?> clazz = instance.getClass();
            // 从缓存中拿
            String key = clazz.getName() + ":" + request.getMethodName() + ":" + (request.getArgs() == null ? 0 : request.getArgs().length);
            MethodInfo methodInfo = map.get(key);
            // 寻找方法
            if (methodInfo == null) {
                // 无参方法
                if (request.getArgs() == null || request.getArgs().length == 0) {
                    methodInfo = new MethodInfo(clazz.getMethod(request.getMethodName()), null);
                } else {
                    // 有参时，根据参数个数寻找方法
                    Method[] allMethod = clazz.getMethods();
                    for (Method m : allMethod) {
                        // 排除方法名不同的
                        if (!m.getName().equals(request.getMethodName())) {
                            continue;
                        }
                        // 形参表
                        Parameter[] ps = m.getParameters();
                        // 长度相同则视为目标方法
                        if (ps.length == request.getArgs().length) {
                            methodInfo = new MethodInfo(m, ps);
                            break;
                        }
                    }
                }
                // 未找到方法
                if (methodInfo == null) {
                    return GrpcResponse.Status.error("No such method:" + request.getMethodName());
                }
                // 缓存方法
                map.put(key, methodInfo);
            }

            Method method = methodInfo.method;
            // 无参方法调用
            if (methodInfo.parameters == null) {
                return GrpcResponse.Status.success(method.invoke(instance));
            }
            // 带参方法调用
            Object[] params = transParam(methodInfo.parameters, request.getArgs());
            return GrpcResponse.Status.success(method.invoke(instance, params));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return GrpcResponse.Status.error(e.getMessage());
        }
    }

    /**
     * 转换参数类型，将jsonElement转为对应类型
     *
     * @param ps   形参信息
     * @param args 参数数据
     * @return 方法参数数据
     */
    private Object[] transParam(Parameter[] ps, Object[] args) {
        Object[] params = new Object[ps.length];
        for (int i = 0; i < ps.length; i++) {
            Parameter p = ps[i];
            params[i] = gson.fromJson((JsonElement) (args[i]), p.getType());
        }
        return params;
    }

    private static class MethodInfo {
        private Method method;
        private Parameter[] parameters;

        MethodInfo(Method method, Parameter[] parameters) {
            this.method = method;
            this.parameters = parameters;
        }

    }

}
