package top.cheivin.grpc.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务信息定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInfo {
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 服务版本
     */
    private String version;
    /**
     * 接口类
     */
    private Class clz;
    /**
     * 别名
     */
    private String alias;
    /**
     * 权重
     */
    private int weight;
}
