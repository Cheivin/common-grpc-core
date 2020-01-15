package top.cheivin.grpc.handle.serialize;

import com.google.gson.*;
import top.cheivin.grpc.core.GrpcRequest;
import top.cheivin.grpc.core.GrpcResponse;
import top.cheivin.grpc.handle.DataFormat;
import top.cheivin.grpc.util.gson.DateTypeAdapter;
import top.cheivin.grpc.util.gson.LocalDateTimeTypeAdapter;
import top.cheivin.grpc.util.gson.TimestampTypeAdapter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * json序列化
 *
 * @author cheivin
 * @date 2020/1/14
 */
public class JsonSerializer implements Serializer {
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateTypeAdapter())
            .registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();

    @Override
    public DataFormat getDataFormat() {
        return DataFormat.JSON;
    }

    @Override
    public <T> byte[] serialize(T obj) {
        return gson.toJson(obj).getBytes();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (clazz.isAssignableFrom(GrpcRequest.class)) {
            return (T) deserializeRequest(bytes);
        }
        if (clazz.isAssignableFrom(GrpcResponse.class)) {
            return (T) deserializeResponse(bytes);
        }
        return gson.fromJson(new String(bytes), clazz);
    }

    private GrpcRequest deserializeRequest(byte[] bytes) {
        String json = new String(bytes);
        JsonObject jsonObj = gson.fromJson(json, JsonObject.class);

        GrpcRequest request = new GrpcRequest();
        request.setServiceName(jsonObj.get("serviceName").getAsString());
        request.setVersion(jsonObj.get("version").getAsString());
        request.setMethodName(jsonObj.get("methodName").getAsString());

        JsonElement argsElement = jsonObj.get("args");
        if (argsElement == null) {
            return request;
        }
        JsonArray argsArr = argsElement.getAsJsonArray();
        JsonElement[] elements = new JsonElement[argsArr.size()];
        for (int i = 0; i < elements.length; i++) {
            elements[i] = argsArr.get(i);
        }
        request.setArgs(elements);
        return request;
    }

    private GrpcResponse deserializeResponse(byte[] bytes) {
        String json = new String(bytes);
        JsonObject jsonObj = gson.fromJson(json, JsonObject.class);

        GrpcResponse response = new GrpcResponse();
        response.setStatus(jsonObj.get("status").getAsInt());
        JsonElement messageObj = jsonObj.get("message");
        if (messageObj == null) {
            response.setMessage(null);
        } else {
            response.setMessage(messageObj.getAsString());
        }
        response.setResult(jsonObj.get("result"));

        return response;
    }
}
