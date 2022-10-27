package http.deserializers;

import com.google.gson.*;
import entity.Epic;
import entity.Subtask;
import entity.TaskStatus;
import entity.TaskType;
import manager.TaskManager;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EpicDeserializer implements JsonDeserializer<Epic> {
    private final TaskManager taskManager;
    private final DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public EpicDeserializer(TaskManager manager) {
        this.taskManager = manager;
    }

    @Override
    public Epic deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        Epic epic = null;
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Subtask.class, new SubtaskDeserializer(taskManager))
                .create();

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String name = jsonObject.get("name").getAsString();
            TaskStatus status = TaskStatus.valueOf(jsonObject.get("status").getAsString().toUpperCase());
            TaskType typeOfTasks = TaskType.valueOf(jsonObject.get("type").getAsString().toUpperCase());
            String description = jsonObject.get("description").getAsString();
            LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("start time").getAsString(), formatterTime);
            int duration = jsonObject.get("duration").getAsInt();
            JsonArray subtasksJson = jsonObject.getAsJsonArray("subtask list");
            //       for (int i = 0; i < subtasksJson.length(); )
            List<Subtask> subtasks = new ArrayList<>();
            for (int i = 0; i < subtasksJson.size(); i++) {
                JsonObject subtaskJson = subtasksJson.get(i).getAsJsonObject();
                Subtask subtask = gson.fromJson(subtaskJson, Subtask.class);
                subtasks.add(subtask);
            }
            epic = new Epic(name, description, typeOfTasks, status, startTime , duration);
            epic.setEpicSubtask(subtasks);
        }
        return epic;
    }
}
