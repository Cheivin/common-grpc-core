package top.cheivin.grpc.handle.caller;

import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import top.cheivin.grpc.core.*;
import top.cheivin.grpc.exception.ChannelException;
import top.cheivin.grpc.exception.InvokeException;
import top.cheivin.grpc.handle.Caller;
import top.cheivin.grpc.serialize.ProtoBufSerializer;
import top.cheivin.grpc.serialize.Serializer;
import top.cheivin.grpc.serialize.SerializerFactory;
import top.cheivin.grpc.util.IdWorker;

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

    private Serializer serializer = new ProtoBufSerializer();

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
            byte[] bytes = serializer.serialize(grpcRequest);
            GrpcService.Request request = GrpcService.Request.newBuilder()
                    .setRequest(ByteString.copyFrom(bytes))
                    .setMsgId(String.valueOf(IdWorker.next()))
                    .setLang(LANG)
                    .setFormat(serializer.getDataFormat())
                    .build();
            // 获取结果
            GrpcService.Response response = blockingStub.handle(request);
            // 获取解析器
            Serializer respSerializer = SerializerFactory.getSerializer(response.getFormat());
            return respSerializer.deserialize(response.getReponse().toByteArray(), GrpcResponse.class);
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
