package top.cheivin.grpc.loadbalance;

import top.cheivin.grpc.core.RemoteInstance;

import java.util.Set;

/**
 *
 */
public interface LoadBalance {
    /**
     * 添加实例
     *
     * @param instance 实例对象
     */
    boolean addInstance(final RemoteInstance instance);

    /**
     * 移除实例
     *
     * @param instanceId 实例ID
     */
    void removeInstance(String instanceId);


    /**
     * 获取所有实例id
     * @return id列表
     */
    Set<String> getIds();

    /**
     * 选择实例
     *
     * @return 提供者实例
     */
    RemoteInstance choose();

    /**
     * 销毁均衡
     */
    void destroy();
}
