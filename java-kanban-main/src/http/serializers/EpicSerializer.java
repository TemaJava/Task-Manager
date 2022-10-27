package http.serializers;

import com.google.gson.*;
import entity.Epic;
import entity.Subtask;


import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

public class EpicSerializer implements JsonSerializer<Epic> {

    @Override
    public JsonElement serialize(Epic epic, Type type, JsonSerializationContext jsonSerializationContext) {
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        JsonObject result = new JsonObject();

        result.addProperty("id", String.valueOf(epic.getId()));
        result.addProperty("name", epic.getName());
        result.addProperty("description", epic.getDescription());
        result.addProperty("status", String.valueOf(epic.getStatus()));
        result.addProperty("type", String.valueOf(epic.getType()));
        JsonArray subtaskList = new JsonArray();
        result.addProperty("start time", epic.getStartTime().format(formatterTime));
        result.addProperty("duration", String.valueOf(epic.getDuration()));
        result.addProperty("subtask list", String.valueOf(subtaskList));
        if (!epic.getEpicSubtasks().isEmpty()) {
            for (Subtask subtask : epic.getEpicSubtasks()) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
                        .create();
                subtaskList.add(gson.toJson(subtask));
            }

        }
        return result;
    }
}
