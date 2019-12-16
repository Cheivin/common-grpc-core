package top.cheivin.grpc.handle;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import top.cheivin.grpc.core.*;

/**
 * 服务调用器
 */
public class GrpcHandler extends CommonServiceGrpc.CommonServiceImplBase {
    private Invoker invoker;
    private ServiceInfoManage manage;

    public GrpcHandler(Invoker invoker, ServiceInfoManage manage) {
        if (invoker == null) {
            this.invoker = new DefaultInvoker();
        } else {
            this.invoker = invoker;
        }
        this.manage = manage;
    }

    @Override
    public void handle(GrpcService.Request gRpcRequest, StreamObserver<GrpcService.Response> observer) {
        // 解析请求数据
        GrpcRequest request = invoker.parseRequest(gRpcRequest.getRequest().toByteArray());
        // 执行调用
        try {
            Object instance = manage.getInstance(request);
            if (instance == null) {
                response(observer, GrpcResponse.Status.error("Service not registered"));
            } else {
                response(observer, invoker.invoke(instance, request));
            }
        } catch (Exception e) {
            response(observer, GrpcResponse.Status.error(e.getMessage()));
        }
    }

    /**
     * 返回结果
     *
     * @param observer 数据流
     * @param response result
     */
    private void response(StreamObserver<GrpcService.Response> observer, GrpcResponse response) {
        // 打包结果数据
        ByteString bytes = ByteString.copyFrom(invoker.packResponse(response));
        // 返回结果
        observer.onNext(GrpcService.Response.newBuilder().setReponse(bytes).build());
        observer.onCompleted();
    }

}
