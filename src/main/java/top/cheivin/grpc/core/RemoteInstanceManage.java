package top.cheivin.grpc.core;

import top.cheivin.grpc.exception.ChannelException;
import top.cheivin.grpc.exception.InstanceException;
import top.cheivin.grpc.loadbalance.LoadBalance;
import top.cheivin.grpc.loadbalance.LoadBalanceFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * 远程服务提供者实例管理器
 */
public class RemoteInstanceManage {
    private static final Logger log = Logger.getLogger(RemoteInstanceManage.class.getName());
    /**
     * 负载均衡类型
     */
    private int loadBalanceType = -1;
    /**
     * 服务实例map
     */
    private ConcurrentHashMap<String, LoadBalance> serviceInstanceMap = new ConcurrentHashMap<>();

    public RemoteInstanceManage() {
    }

    public RemoteInstanceManage(int loadBalanceType) {
        this.loadBalanceType = loadBalanceType;
    }

    public void clear() {
        serviceInstanceMap.values().forEach(LoadBalance::destroy);
        serviceInstanceMap.clear();
    }

    public void clear(String serviceKey) {
        LoadBalance loadBalance = serviceInstanceMap.remove(serviceKey);
        if (loadBalance != null) {
            loadBalance.destroy();
        }
    }

    public RemoteInstance getInstance(String serviceKey) throws InstanceException, ChannelException {
        LoadBalance loadBalance = serviceInstanceMap.get(serviceKey);
        if (loadBalance == null) {
            throw new InstanceException("no instance exist");
        }
        RemoteInstance remoteInstance = loadBalance.choose();
        if (remoteInstance == null) {
            throw new InstanceException("no instance choose");
        }
        if (remoteInstance.isClosed()) {
            throw new ChannelException("channel is closed", remoteInstance);
        }
        return remoteInstance;
    }

    /**
     * 刷新服务列表
     *
     * @param serviceKeys 服务key列表
     * @return s[0]:移除的服务key，s[1]:新增的服务key
     */
    public String[][] refreshServiceList(List<String> serviceKeys) {
        /*
         * 先处理移除的服务
         * 1.将本地map的服务key值放入临时set
         * 2.传入的服务key列表从临时set中移除
         * 3.临时set中剩余的则是被移除的服务列表
         * 4.调用移除方法处理instance
         */
        HashSet<String> serviceSet = new HashSet<>(serviceInstanceMap.keySet());
        serviceSet.removeAll(serviceKeys);
        serviceSet.forEach(this::removeService);
        /*
         * 处理新增的服务
         * 1.将本地map的服务key值从传入的服务key列表中移除
         * 2.list中剩余的则是提供者节点有变动的服务key列表
         * 3.将范围添加至map中
         */
        serviceKeys.removeAll(serviceInstanceMap.keySet());
        serviceKeys.forEach(this::addService);
        // 返回结果
        return new String[][]{serviceSet.toArray(new String[]{}), serviceKeys.toArray(new String[]{})};
    }

    public boolean addService(String serviceKey) {

        return this.serviceInstanceMap.putIfAbsent(
                serviceKey,
                LoadBalanceFactory.getBalance(loadBalanceType)
        ) == null;
    }

    public void removeService(String serviceKey) {
        LoadBalance loadBalance = serviceInstanceMap.remove(serviceKey);
        if (loadBalance != null) {
            loadBalance.destroy();
        }
    }

    public void refreshInstances(String serviceKey, Map<String, RemoteInstance> remoteInstanceMap) {
        LoadBalance loadBalance = serviceInstanceMap.get(serviceKey);
        if (loadBalance == null) {
            return;
        }
        /*
         * 处理移除的节点
         * 1.将本地map的节点实例id列表放入临时set
         * 2.将传入节点实例id从临时set中移除
         * 3.临时set中剩余的则是本地map中不存在的instance
         * 4.调用移除方法将不存在的provider移除
         */
        Set<String> instanceIds = loadBalance.getIds();
        instanceIds.removeAll(remoteInstanceMap.keySet());
        for (String instanceId : instanceIds) {
            log.info("remove remote service: " + instanceId);
            loadBalance.removeInstance(instanceId);
        }
        /*
         * 处理新增的节点
         * 1.将本地map的节点实例id列表从传入节点实例id的临时set中移除
         * 2.临时set中剩余的则是有变动的instance
         * 3.添加变动的instance至LoadBalance中
         */
        remoteInstanceMap.keySet().removeAll(loadBalance.getIds());
        for (RemoteInstance instance : remoteInstanceMap.values()) {
            if (loadBalance.addInstance(instance)) {
                log.info("add remote service: " + instance);
                instance.connect();
            }
        }
    }

    @Override
    public String toString() {
        return "RemoteInstanceManage{" + serviceInstanceMap + '}';
    }
}
