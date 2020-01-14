package top.cheivin.grpc.handle;

/**
 * 数据序列化格式
 *
 * @author cheivin
 * @date 2020/1/14
 */
public enum DataFormat {
    JAVA_BYTES,
    JSON,
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
