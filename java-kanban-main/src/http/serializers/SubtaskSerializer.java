package http.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import entity.Subtask;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

public class SubtaskSerializer implements JsonSerializer<Subtask> {
    @Override
    public JsonElement serialize(Subtask subtask, Type type, JsonSerializationContext jsonSerializationContext) {
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        JsonObject result = new JsonObject();

        result.addProperty("id", String.valueOf(subtask.getId()));
        result.addProperty("name", subtask.getName());
        result.addProperty("description", subtask.getDescription());
        result.addProperty("status", String.valueOf(subtask.getStatus()));
        result.addProperty("type", String.valueOf(subtask.getType()));
        result.addProperty("start time", subtask.getStartTime().format(formatterTime));
        result.addProperty("duration", String.valueOf(subtask.getDuration()));
        result.addProperty("epic id", String.valueOf(subtask.getEpicId()));
        return result;
    }
}
