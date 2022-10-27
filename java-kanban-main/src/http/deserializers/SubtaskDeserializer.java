package http.deserializers;

import com.google.gson.*;
import entity.Subtask;
import entity.TaskStatus;
import entity.TaskType;
import manager.TaskManager;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubtaskDeserializer implements JsonDeserializer<Subtask> {
    private final DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final TaskManager taskManager;

    public SubtaskDeserializer(TaskManager manager) {
        this.taskManager = manager;
    }

    @Override
    public Subtask deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        Subtask subtask = null;
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String name = jsonObject.get("name").getAsString();
            int epicId = jsonObject.get("epic id").getAsInt();
            TaskStatus status = TaskStatus.valueOf(jsonObject.get("status").getAsString());
            TaskType typeOfTasks = TaskType.valueOf(jsonObject.get("type").getAsString());
            LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("start time").getAsString(), formatterTime);
            String description = jsonObject.get("description").getAsString();
            int duration = jsonObject.get("duration").getAsInt();
            subtask = new Subtask(name, description, typeOfTasks, status, epicId, startTime, duration);
        }
        return subtask;
    }
}