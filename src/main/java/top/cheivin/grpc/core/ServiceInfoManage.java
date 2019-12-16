package top.cheivin.grpc.core;

import java.util.Collection;

/**
 * 服务信息管理器，用于调用时，返回执行对象实例
 */
public interface ServiceInfoManage {
    /**
     * 获取执行调用的实例
     *
     * @param request 请求信息
     * @return 执行调用实例
     */
    Object getInstance(GrpcRequest request);

    Collection<ServiceInfo> getServiceInfos();
}
