package top.cheivin.grpc.handle;

import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import top.cheivin.grpc.core.*;
import top.cheivin.grpc.exception.ChannelException;
import top.cheivin.grpc.exception.InvokeException;
import top.cheivin.grpc.util.ProtoBufUtils;

/**
 * 远程调用器，用于客户端远程调用服务请求结果
 */
@Slf4j
public class DefaultCaller implements Caller {
    private int retry;

    public DefaultCaller() {
        this.retry = 1;
    }

    public DefaultCaller(int retry) {
        this.retry = retry;
    }

    @Override
    public GrpcResponse call(RemoteInstance remoteInstance, GrpcRequest grpcRequest) throws InvokeException {
        return call(remoteInstance, grpcRequest, this.retry);
    }

    public GrpcResponse call(RemoteInstance remoteInstance, GrpcRequest grpcRequest, int retry) throws InvokeException {
        // 检查channel存活
        if (remoteInstance.isClosed()) {
            throw new ChannelException("channel is not available");
        }
        // 获取连接
        CommonServiceGrpc.CommonServiceBlockingStub blockingStub = CommonServiceGrpc
                .newBlockingStub(remoteInstance.getChannel()).withWaitForReady();
        try {
            // 远程调用
            byte[] bytes = ProtoBufUtils.serialize(grpcRequest);
            GrpcService.Request request = GrpcService.Request.newBuilder().setRequest(ByteString.copyFrom(bytes)).build();
            ByteString response = blockingStub.handle(request).getReponse();
            return ProtoBufUtils.deserialize(response.toByteArray(), GrpcResponse.class);
        } catch (Exception e) {
            if (!(e instanceof StatusRuntimeException) && !e.getMessage().contains("DEADLINE_EXCEEDED")) {
                throw e;
            }
            if (retry <= 0) {
                log.error("max retry count, server context handle fail,service address:{},serviceName:{},version:{},methodName:{},args:{},error message:{}"
                        , remoteInstance.getIp() + ":" + remoteInstance.getPort(), grpcRequest.getServiceName(), grpcRequest.getVersion(), grpcRequest.getMethodName(), grpcRequest.getArgs(), "max retry count");
                throw new InvokeException("max retry count");
            }
            // 重试
            return call(remoteInstance, grpcRequest, --retry);
        }
    }

}
