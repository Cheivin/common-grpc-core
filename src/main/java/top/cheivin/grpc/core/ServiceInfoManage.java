package top.cheivin.grpc.core;

import top.cheivin.grpc.annotation.GrpcService;

import java.util.Collection;

/**
 * 服务信息管理器，用于调用时，返回执行对象实例
 */
public interface ServiceInfoManage {
    /**
     * 根据对象添加服务
     *
     * @param obj 实例对象
     */
    default boolean addService(Object obj) {
        return addService(obj.getClass());
    }

    /**
     * 根据类添加服务
     *
     * @param clz 实例对象
     */
    default boolean addService(Class clz) {
        top.cheivin.grpc.annotation.GrpcService info;
        out:
        do {
            // 从当前类找注解
            info = (top.cheivin.grpc.annotation.GrpcService) clz.getAnnotation(top.cheivin.grpc.annotation.GrpcService.class);
            if (info == null) {
                // 从当前类接口找注解
                Class[] interfaces = clz.getInterfaces();
                for (Class anInterface : interfaces) {
                    info = (top.cheivin.grpc.annotation.GrpcService) anInterface.getAnnotation(GrpcService.class);
                    if (info != null) {
                        break out;
                    }
                }
                // 向上找父类
                clz = clz.getSuperclass();
            }
            // 父类为顶级类则跳出查找
        } while (!clz.equals(Object.class));
        if (info == null) {
            return false;
        }
        return addService(ServiceInfo.builder()
                .clz(clz)
                .serviceName("".equals(info.service()) ? clz.getSimpleName() : info.service())
                .version(info.version())
                .weight(info.weight())
                .alias("".equals(info.alias()) ? clz.getSimpleName() : info.alias())
                .build());
    }

    /**
     * 根据服务信息添加服务
     *
     * @param serviceInfo 实例对象
     */
    boolean addService(ServiceInfo serviceInfo);

    /**
     * 获取执行调用的实例
     *
     * @param request 请求信息
     * @return 执行调用实例
     */
    Object getInstance(GrpcRequest request);

    Collection<ServiceInfo> getServiceInfos();

}
