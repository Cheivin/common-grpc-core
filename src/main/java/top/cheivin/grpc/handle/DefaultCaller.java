package top.cheivin.grpc.handle;

import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import top.cheivin.grpc.core.*;
import top.cheivin.grpc.exception.ChannelException;
import top.cheivin.grpc.exception.InvokeException;
import top.cheivin.grpc.util.IdWorker;
import top.cheivin.grpc.util.ProtoBufUtils;

/**
 * 远程调用器，用于客户端远程调用服务请求结果
 */
public class DefaultCaller implements Caller {
    private int retry;

    public DefaultCaller() {
        this.retry = 1;
    }

    public DefaultCaller(int retry) {
        this.retry = retry;
    }

    @Override
    public String getDataFormat() {
        return "BYTES";
    }

    @Override
    public GrpcResponse call(RemoteInstance remoteInstance, GrpcRequest grpcRequest) throws InvokeException {
        return call(remoteInstance, grpcRequest, this.retry);
    }

    public GrpcResponse call(RemoteInstance remoteInstance, GrpcRequest grpcRequest, int retry) throws InvokeException {
        // 检查channel存活
        if (remoteInstance.isClosed()) {
            throw new ChannelException("channel is closed", remoteInstance);
        }
        // 获取连接
        CommonServiceGrpc.CommonServiceBlockingStub blockingStub = CommonServiceGrpc
                .newBlockingStub(remoteInstance.getChannel()).withWaitForReady();
        try {
            // 远程调用
            byte[] bytes = ProtoBufUtils.serialize(grpcRequest);
            GrpcService.Request request = GrpcService.Request.newBuilder()
                    .setRequest(ByteString.copyFrom(bytes))
                    .setMsgId(String.valueOf(IdWorker.next()))
                    .setLang(LANG)
                    .setFormat(getDataFormat())
                    .build();
            ByteString response = blockingStub.handle(request).getReponse();
            return ProtoBufUtils.deserialize(response.toByteArray(), GrpcResponse.class);
        } catch (Exception e) {
            if (!(e instanceof StatusRuntimeException) && !e.getMessage().contains("DEADLINE_EXCEEDED")) {
                throw new InvokeException("error", e, remoteInstance, grpcRequest);
            }
            if (retry <= 0) {
                throw new InvokeException("max retry count", remoteInstance, grpcRequest);
            }
            // 重试
            return call(remoteInstance, grpcRequest, --retry);
        }
    }

}
