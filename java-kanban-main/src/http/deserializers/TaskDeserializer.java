package http.deserializers;

import com.google.gson.*;
import entity.Task;
import entity.TaskStatus;
import entity.TaskType;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskDeserializer implements JsonDeserializer<Task> {
    private final DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public Task deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        Task task = null;
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String name = jsonObject.get("name").getAsString();
            String description = jsonObject.get("description").getAsString();
            TaskStatus status = TaskStatus.valueOf(jsonObject.get("status").getAsString().toUpperCase());
            TaskType typeOfTasks = TaskType.valueOf(jsonObject.get("type").getAsString().toUpperCase());
            LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("start time").getAsString(), formatterTime);
            int duration = jsonObject.get("duration").getAsInt();
            task = new Task(name, description, typeOfTasks, status, startTime, duration);
        }
        return task;
    }
}
