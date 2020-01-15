package top.cheivin.grpc.handle;

/**
 * 数据序列化格式
 *
 * @author cheivin
 * @date 2020/1/14
 */
public enum DataFormat {
    /**
     * ProtoBuf序列化
     */
    JAVA_BYTES,
    /**
     * Json序列化
     */
    JSON,
    /**
     * Xml序列化(暂不支持)
     */
    XML;


    public String getVal() {
        return this.name();
    }

    public static DataFormat valOf(String name) {
        for (DataFormat value : DataFormat.values()) {
            if (value.getVal().equals(name)) {
                return value;
            }
        }
        return JAVA_BYTES;
    }
}
