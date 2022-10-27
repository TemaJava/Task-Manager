package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import entity.Epic;
import entity.Subtask;
import entity.Task;
import exceptions.ManagerSaveException;
import http.KVTaskClient;
import http.LocalDateAdapter;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager {
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
            new LocalDateAdapter()).create();
    private final KVTaskClient taskClient;

    public HttpTaskManager(URI uri) {
        super(null);
        try {
            taskClient = new KVTaskClient(uri);
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка при подключении к KVServer");
        }
    }

    @Override
    public void save() {
        try {
            taskClient.put("tasks", gson.toJson(taskMap));
            taskClient.put("epics", gson.toJson(epicMap));
            taskClient.put("subtasks", gson.toJson(subtaskMap));
            taskClient.put("history", gson.toJson(historyManager.getHistory()));
            taskClient.put("startId", gson.toJson(generatorId));
        } catch (IOException | InterruptedException err) {
            throw new ManagerSaveException("Ошибка при сохранении данных на KVServer");
        }
    }

    @Override
    public void load() {
        try {
            Map<Integer, Task> tasks = gson.fromJson(taskClient.load("tasks"),
                    new TypeToken<HashMap<Integer, Task>>() {
                    }.getType()
            );
            Map<Integer, Epic> epics = gson.fromJson(taskClient.load("epics"),
                    new TypeToken<HashMap<Integer, Epic>>() {
                    }.getType()
            );
            Map<Integer, Subtask> subtasks = gson.fromJson(taskClient.load("subtasks"),
                    new TypeToken<HashMap<Integer, Subtask>>() {
                    }.getType()
            );
            List<Task> historyList = gson.fromJson(taskClient.load("history"), new TypeToken<List<Task>>() {
                    }.getType()
            );
            HistoryManager history = new InMemoryHistoryManager();
            historyList.forEach(history::add);
            int startId = Integer.parseInt(taskClient.load("startId"));
            this.taskMap = tasks;
            this.epicMap = epics;
            this.subtaskMap = subtasks;
            this.historyManager = history;
            this.startTimeSet.addAll(tasks.values());
            this.startTimeSet.addAll(subtasks.values());
            this.setGeneratorId(startId);
        } catch (IOException | InterruptedException exception) {
            System.out.println("Ошибка при восстановлении данных на KVserver");
        }
    }
}
