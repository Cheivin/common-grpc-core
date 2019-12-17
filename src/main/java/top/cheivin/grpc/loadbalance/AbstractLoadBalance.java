package top.cheivin.grpc.loadbalance;

import top.cheivin.grpc.core.RemoteInstance;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 负载均衡器
 */
public abstract class AbstractLoadBalance implements LoadBalance {
    private ConcurrentHashMap<String, RemoteInstance> instances = new ConcurrentHashMap<>();

    /**
     * 根据实例id获取实例
     *
     * @param idx 实例id
     * @return 实例
     */
    final RemoteInstance get(int idx) {
        return (RemoteInstance) this.instances.values().toArray()[idx];
    }

    /**
     * 根据实例id获取实例
     *
     * @param instanceId 实例id
     * @return 实例
     */
    final RemoteInstance get(String instanceId) {
        return this.instances.get(instanceId);
    }

    /**
     * 实例数
     *
     * @return 个数
     */
    final int size() {
        return this.instances.size();
    }

    /**
     * 添加
     *
     * @param instance 实例f
     */
    void add(final RemoteInstance instance) {
    }

    /**
     * 移除
     *
     * @param instanceId 实例id
     */
    void remove(final RemoteInstance instanceId) {
    }

    /**
     * 添加实例
     *
     * @param instance 实例对象
     * @return 成功
     */
    @Override
    public final boolean addInstance(final RemoteInstance instance) {
        if (this.instances.putIfAbsent(instance.getId(), instance) == null) {
            add(instance);
            instance.connect();
            return true;
        }
        return false;
    }

    /**
     * 移除实例
     *
     * @param instanceId 实例ID
     */
    @Override
    public final void removeInstance(String instanceId) {
        RemoteInstance instance = this.instances.remove(instanceId);
        if (instance != null) {
            remove(instance);
            instance.close();
        }
    }

    @Override
    public Set<String> getIds() {
        return new HashSet<>(this.instances.keySet());
    }

    /**
     * 销毁，清除所有实例
     */
    @Override
    public final void destroy() {
        this.instances.values().forEach(RemoteInstance::close);
        this.instances.clear();
    }

    @Override
    public String toString() {
        return "LoadBalance={" + instances + '}';
    }
}
