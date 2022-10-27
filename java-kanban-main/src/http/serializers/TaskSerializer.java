package http.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import entity.Task;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

public class TaskSerializer implements JsonSerializer<Task> {
    @Override
    public JsonElement serialize(Task task, Type type, JsonSerializationContext jsonSerializationContext) {
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        JsonObject result = new JsonObject();
        result.addProperty("id", String.valueOf(task.getId()));
        result.addProperty("name", task.getName());
        result.addProperty("description", task.getDescription());
        result.addProperty("status", String.valueOf(task.getStatus()));
        result.addProperty("type", String.valueOf(task.getType()));
        result.addProperty("start time", task.getStartTime().format(formatterTime));
        result.addProperty("duration", String.valueOf(task.getDuration()));
        return result;
    }
}
