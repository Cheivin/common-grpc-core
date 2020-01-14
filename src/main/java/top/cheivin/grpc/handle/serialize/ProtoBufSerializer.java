package top.cheivin.grpc.handle.serialize;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;
import top.cheivin.grpc.handle.DataFormat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ProtoBuf序列化
 *
 * @author cheivin
 * @date 2020/1/13
 */
public class ProtoBufSerializer implements Serializer {

    /**
     * 缓存 schema 对象的 map
     */
    private static Map<Class<?>, RuntimeSchema<?>> cachedSchema = new ConcurrentHashMap<>();

    /**
     * 根据获取相应类型的schema方法
     */
    @SuppressWarnings({"unchecked"})
    private <T> RuntimeSchema<T> getSchema(Class<T> clazz) {
        RuntimeSchema<T> schema = (RuntimeSchema<T>) cachedSchema.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(clazz);
            cachedSchema.put(clazz, schema);
        }
        return schema;
    }

    @Override
    public DataFormat getDataFormat() {
        return DataFormat.JAVA_BYTES;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> byte[] serialize(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        RuntimeSchema<T> schema = getSchema(clazz);
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        RuntimeSchema<T> schema = RuntimeSchema.createFrom(clazz);
        T message = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, message, schema);
        return message;
    }
}
