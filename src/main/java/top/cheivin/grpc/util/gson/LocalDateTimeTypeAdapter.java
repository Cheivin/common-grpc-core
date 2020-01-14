package top.cheivin.grpc.util.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 本地时间类型转换
 *
 * @author cheivin
 * @date 2020/1/14
 */
public class LocalDateTimeTypeAdapter implements JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return LocalDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(jsonElement.getAsJsonPrimitive().getAsString()));
    }

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(localDateTime.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }
}
