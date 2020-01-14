package top.cheivin.grpc.util.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期类型转换
 *
 * @author cheivin
 * @date 2020/1/14
 */
public class DateTypeAdapter implements JsonDeserializer<Date>, JsonSerializer<Date> {
    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        LocalDateTime ldt = LocalDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(jsonElement.getAsJsonPrimitive().getAsString()));
        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    @Override
    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(date.toInstant()
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }
}
