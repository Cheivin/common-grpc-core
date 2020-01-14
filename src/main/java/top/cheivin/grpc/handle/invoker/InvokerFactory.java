package top.cheivin.grpc.handle.invoker;

import top.cheivin.grpc.handle.DataFormat;
import top.cheivin.grpc.handle.Invoker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 反射调用类工厂
 *
 * @author cheivin
 * @date 2020/1/14
 */
public class InvokerFactory {
    private static Map<String, Invoker> map = new ConcurrentHashMap<>();

    /**
     * 初始化
     */
    static {
        map.put(DataFormat.JAVA_BYTES.getVal(), new ProtoBufInvoker());
        map.put(DataFormat.JSON.getVal(), new JsonInvoker());
    }

    public static Invoker getInvoker(DataFormat dataFormat) {
        return map.get(dataFormat.getVal());
    }
}
