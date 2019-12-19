package top.cheivin.grpc.core;

import top.cheivin.grpc.exception.InstanceException;

import java.util.Collection;
import java.util.HashMap;

/**
 * 默认管理器
 */
public class DefaultServiceInfoManage implements ServiceInfoManage {
    private HashMap<String, ServiceInfo> serviceInfoMap = new HashMap<>();
    private HashMap<String, Object> instanceMap = new HashMap<>();

    @Override
    public Object getInstance(GrpcRequest request) {
        return instanceMap.get(request.getServiceName() + ":" + request.getVersion());
    }

    @Override
    public Collection<ServiceInfo> getServiceInfos() {
        return serviceInfoMap.values();
    }

    public final boolean addService(ServiceInfo info) {
        String key = info.getServiceName() + ":" + info.getVersion();
        serviceInfoMap.put(key, info);
        try {
            return instanceMap.putIfAbsent(key, info.getClz().newInstance()) == null;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new InstanceException(e);
        }
    }
}
