package top.cheivin.grpc;

import top.cheivin.grpc.handle.GrpcHandler;
import top.cheivin.grpc.core.Registry;
import top.cheivin.grpc.core.ServiceInfoManage;
import top.cheivin.grpc.handle.Invoker;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.NettyServerBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * gRPC服务端
 */
@Slf4j
public class GrpcServer {
    /**
     * gRPC服务端
     */
    private Server server;
    /**
     * 注册中心
     */
    private Registry registry;
    /**
     * 服务信息管理器
     */
    private ServiceInfoManage serviceInfoManage;

    private GrpcServer() {
    }

    /**
     * 启动
     *
     * @throws Exception 异常
     */
    public void start() throws Exception {
        server.start();
        // 注册服务
        registry.start(server.getPort());
        registry.addService(serviceInfoManage.getServiceInfos());
        // 应用关闭时关闭监听
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    /**
     * 停止
     */
    public void stop() {
        registry.close();
        server.shutdown();
    }

    public static Builder from(Registry registry, ServiceInfoManage serviceManage) {
        return new Builder(registry, serviceManage);
    }

    public static class Builder {
        private GrpcServer grpcServer;
        private Invoker invoker = null;
        private int port = 29999;
        /**
         * 默认2H
         */
        private long keepAliveTime = 7200000;
        /**
         * 默认20s
         */
        private long keepAliveTimeout = 20000;
        private boolean permit = false;

        private Builder(Registry registry, ServiceInfoManage serviceManage) {
            this.grpcServer = new GrpcServer();
            this.grpcServer.registry = registry;
            this.grpcServer.serviceInfoManage = serviceManage;
        }

        public void port(int port) {
            this.port = port;
        }

        public void invoker(Invoker invoker) {
            this.invoker = invoker;
        }

        /**
         * 发送keepalive ping的时间间隔，单位毫秒
         */
        public void keepAliveTime(long keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
        }

        /**
         * keepalive ping的发送方等待确认的时间，如果在此时间内未收到确认，它将关闭连接。单位毫秒
         */
        public void keepAliveTimeout(long keepAliveTimeout) {
            this.keepAliveTimeout = keepAliveTimeout;
        }

        /**
         * 如果将此通道参数设置为true，则即使没有请求进行，也可以发送keepalive ping
         */
        public void permitKeepAliveWithoutCalls(boolean permit) {
            this.permit = permit;
        }

        public GrpcServer build() {
            GrpcHandler handler = new GrpcHandler(this.invoker, this.grpcServer.serviceInfoManage);
            ServerBuilder serverBuilder = NettyServerBuilder.forPort(this.port)
                    .addService(handler)
                    .keepAliveTime(this.keepAliveTime, TimeUnit.MILLISECONDS)
                    .keepAliveTimeout(this.keepAliveTimeout, TimeUnit.MILLISECONDS)
                    .permitKeepAliveWithoutCalls(permit);

            this.grpcServer.server = serverBuilder.build();
            return this.grpcServer;
        }
    }

}
