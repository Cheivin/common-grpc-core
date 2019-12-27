package top.cheivin.grpc.handle;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import top.cheivin.grpc.core.*;

/**
 * 服务调用器
 */
public class GrpcHandler extends CommonServiceGrpc.CommonServiceImplBase {
    private static final String LANG = "JAVA";
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
        GrpcResponse response;
        // 执行本地反射调用
        try {
            Object instance = manage.getInstance(request);
            if (instance == null) {
                response = GrpcResponse.Status.error("Service not registered");
            } else {
                response = invoker.invoke(instance, request);
            }
        } catch (Exception e) {
            response = GrpcResponse.Status.error(e.getMessage());
        }
        // 打包结果数据
        ByteString bytes = ByteString.copyFrom(invoker.packResponse(response));
        // 返回结果
        observer.onNext(GrpcService.Response.newBuilder()
                .setReponse(bytes)
                .setMsgId(gRpcRequest.getMsgId())
                .setLang(LANG)
                .setFormat(invoker.getDataFormat())
                .build());
        observer.onCompleted();
    }
}
