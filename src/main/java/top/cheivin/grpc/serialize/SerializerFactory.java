package top.cheivin.grpc.serialize;

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
        ProtoBufSerializer protoBuf = new ProtoBufSerializer();
        serializerMap.put(protoBuf.getDataFormat(), protoBuf);
    }

    public static Serializer getSerializer(String dataFormat) {
        return serializerMap.get(dataFormat);
    }
}
