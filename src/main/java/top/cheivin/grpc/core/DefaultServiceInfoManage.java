package top.cheivin.grpc.core;

import lombok.extern.slf4j.Slf4j;
import top.cheivin.grpc.annotation.GrpcService;
import top.cheivin.grpc.exception.InstanceException;

import java.util.Collection;
import java.util.HashMap;

/**
 * 默认管理器
 */
@Slf4j
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

    public final void addService(ServiceInfo info) throws InstanceException {
        String key = info.getServiceName() + ":" + info.getVersion();
        serviceInfoMap.put(key, info);
        try {
            instanceMap.put(key, info.getClz().newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new InstanceException(e);
        }
    }

    public final void addService(Class clz) throws InstanceException {
        GrpcService info = (GrpcService) clz.getAnnotation(GrpcService.class);
        if (info == null) {
            log.error("Class [{}] not defined by GrpcService", clz.getName());
            return;
        }
        addService(ServiceInfo.builder()
                .clz(clz)
                .serviceName("".equals(info.service()) ? clz.getSimpleName() : info.service())
                .version(info.version())
                .weight(info.weight())
                .alias("".equals(info.alias()) ? clz.getSimpleName() : info.alias())
                .build());
    }
}
