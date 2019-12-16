package top.cheivin.grpc.loadbalance;

import top.cheivin.grpc.core.RemoteInstance;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 负载均衡器
 */
public abstract class LoadBalance {
    private ConcurrentHashMap<String, RemoteInstance> instances = new ConcurrentHashMap<>();

    public final void destroy() {
        this.instances.values().forEach(RemoteInstance::close);
        this.instances.clear();
    }

    /**
     * 获取所有实例
     *
     * @return 实例列表
     */
    public Collection<RemoteInstance> getAll() {
        return this.instances.values();
    }

    /**
     * 根据实例id获取实例
     *
     * @param idx 实例id
     * @return 实例
     */
    final RemoteInstance get(int idx) {
        return (RemoteInstance) this.getAll().toArray()[idx];
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
     * 添加实例
     *
     * @param instance 实例对象
     */
    public final void addInstance(final RemoteInstance instance) {
        this.instances.putIfAbsent(instance.getId(), instance);
        add(instance);
    }

    /**
     * 移除实例
     *
     * @param instanceId 实例ID
     */
    public final void removeInstance(String instanceId) {
        RemoteInstance instance = this.instances.remove(instanceId);
        if (instance != null) {
            remove(instance);
        }
    }

    /**
     * 移除实例
     *
     * @param instance 实例对象
     */
    public final void removeInstance(RemoteInstance instance) {
        this.removeInstance(instance.getId());
    }

    /**
     * 添加
     *
     * @param instance 实例f
     */
    abstract void add(final RemoteInstance instance);

    /**
     * 移除
     *
     * @param instanceId 实例id
     */
    abstract void remove(final RemoteInstance instanceId);

    /**
     * 选择实例
     *
     * @return 提供者实例
     */
    public abstract RemoteInstance choose();

    @Override
    public String toString() {
        return "LoadBalance={" + instances + '}';
    }
}
