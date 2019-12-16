package top.cheivin.grpc.core;

import lombok.Data;
import lombok.ToString;

/**
 * 调用请求参数
 */
@Data
@ToString
public class GrpcRequest {

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 版本
     */
    private String version;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 方法参数
     */
    private Object[] args;
}
