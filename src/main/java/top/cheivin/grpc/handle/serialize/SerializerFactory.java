package top.cheivin.grpc.handle.serialize;

import top.cheivin.grpc.handle.DataFormat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 序列化工具工厂
 *
 * @author cheivin
 * @date 2020/1/13
 */
public class SerializerFactory {
    private static Map<String, Serializer> serializerMap = new ConcurrentHashMap<>();

    /**
     * 初始化
     */
    static {
        serializerMap.put(DataFormat.JAVA_BYTES.getVal(), new ProtoBufSerializer());
        serializerMap.put(DataFormat.JSON.getVal(), new JsonSerializer());
    }

    public static Serializer getSerializer(DataFormat dataFormat) {
        return serializerMap.get(dataFormat.getVal());
    }
}
