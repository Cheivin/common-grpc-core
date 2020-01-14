package top.cheivin.grpc.handle.serialize;

import top.cheivin.grpc.handle.DataFormat;

/**
 * 序列化器
 *
 * @author cheivin
 * @date 2020/1/13
 */
public interface Serializer {

    /**
     * 数据序列化方式
     *
     * @return 大写英文字符
     */
    DataFormat getDataFormat();

    /**
     * 序列化
     *
     * @param obj 对象
     * @return bytes
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化
     *
     * @param bytes bytes
     * @param clazz 类类型
     * @param <T>   目标类
     * @return 类实例
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);

}
