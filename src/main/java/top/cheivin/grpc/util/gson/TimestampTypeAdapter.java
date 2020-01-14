package top.cheivin.grpc.util.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * timestamp类型转换
 *
 * @author cheivin
 * @date 2020/1/14
 */
public class TimestampTypeAdapter implements JsonDeserializer<Timestamp>, JsonSerializer<Timestamp> {
    @Override
    public Timestamp deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        LocalDateTime ldt = LocalDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(jsonElement.getAsJsonPrimitive().getAsString()));
        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
        return Timestamp.from(zdt.toInstant());
    }

    @Override
    public JsonElement serialize(Timestamp timestamp, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(Instant.ofEpochMilli(timestamp.getTime())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }
}
