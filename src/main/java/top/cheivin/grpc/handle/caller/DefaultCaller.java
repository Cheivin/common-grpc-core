package top.cheivin.grpc.handle.caller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import top.cheivin.grpc.core.*;
import top.cheivin.grpc.exception.ChannelException;
import top.cheivin.grpc.exception.InvokeException;
import top.cheivin.grpc.handle.Caller;
import top.cheivin.grpc.handle.DataFormat;
import top.cheivin.grpc.handle.serialize.Serializer;
import top.cheivin.grpc.handle.serialize.SerializerFactory;
import top.cheivin.grpc.util.IdWorker;
import top.cheivin.grpc.util.gson.DateTypeAdapter;
import top.cheivin.grpc.util.gson.LocalDateTimeTypeAdapter;
import top.cheivin.grpc.util.gson.TimestampTypeAdapter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 远程调用器，用于客户端远程调用服务请求结果
 */
public class DefaultCaller implements Caller {
    private static final int DEFAULT_RETRY = 1;
    private static final DataFormat DEFAULT_DATA_FORMAT = DataFormat.JAVA_BYTES;

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateTypeAdapter())
            .registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();

    private int retry;
    private Serializer serializer;

    public DefaultCaller() {
        this(DEFAULT_RETRY);
    }

    public DefaultCaller(int retry) {
        this(retry, DEFAULT_DATA_FORMAT);
    }

    public DefaultCaller(DataFormat dataFormat) {
        this(DEFAULT_RETRY, dataFormat);
    }

    public DefaultCaller(int retry, DataFormat dataFormat) {
        this.retry = retry;
        this.serializer = SerializerFactory.getSerializer(dataFormat);
    }


    @Override
    public <T> T call(RemoteInstance remoteInstance, GrpcRequest request, TypeToken<T> resType) throws InvokeException {
        return call(remoteInstance, request, resType, retry);
    }

    public <T> T call(RemoteInstance remoteInstance, GrpcRequest grpcRequest, TypeToken<T> resType, int retry) throws InvokeException {
        CallResp resp = dealCall(remoteInstance, grpcRequest, retry);
        return parse(resp.dataFormat, resp.response.getResult(), resType);
    }

    private CallResp dealCall(RemoteInstance remoteInstance, GrpcRequest grpcRequest, int retry) throws InvokeException {
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
                    .setFormat(serializer.getDataFormat().getVal())
                    .build();
            // 获取结果
            GrpcService.Response response = blockingStub.handle(request);
            // 获取解析器
            Serializer respSerializer = SerializerFactory.getSerializer(DataFormat.valOf(response.getFormat()));
            GrpcResponse grpcResponse = respSerializer.deserialize(response.getReponse().toByteArray(), GrpcResponse.class);
            if (grpcResponse.isSuccess()) {
                return new CallResp(grpcResponse, respSerializer.getDataFormat());
            } else {
                throw new InvokeException(grpcResponse.getMessage(), remoteInstance, grpcRequest);
            }
        } catch (Exception e) {
            if (!(e instanceof StatusRuntimeException) && !e.getMessage().contains("DEADLINE_EXCEEDED")) {
                throw new InvokeException("error", e, remoteInstance, grpcRequest);
            }
            if (retry <= 0) {
                throw new InvokeException("max retry count", remoteInstance, grpcRequest);
            }
            // 重试
            return dealCall(remoteInstance, grpcRequest, --retry);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T parse(DataFormat dataFormat, Object obj, TypeToken<T> resType) {
        if (dataFormat == DataFormat.JSON) {
            JsonElement jsonEl = (JsonElement) obj;
            return gson.fromJson(jsonEl, resType.getType());
        }
        return (T) obj;
    }

    private static class CallResp {
        GrpcResponse response;
        DataFormat dataFormat;

        CallResp(GrpcResponse response, DataFormat dataFormat) {
            this.response = response;
            this.dataFormat = dataFormat;
        }
    }
}
