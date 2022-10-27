package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.serializers.SubtaskSerializer;
import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entity.Epic;
import entity.Subtask;
import entity.Task;
import entity.TaskType;
import http.deserializers.EpicDeserializer;
import http.deserializers.SubtaskDeserializer;
import http.deserializers.TaskDeserializer;
import http.serializers.EpicSerializer;
import http.serializers.TaskSerializer;
import manager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class TasksHandler implements HttpHandler {
    TaskManager fileManager;

    public TasksHandler(TaskManager taskService) {
        this.fileManager = taskService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Gson serializer = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Task.class, new TaskSerializer())
                .registerTypeAdapter(Epic.class, new EpicSerializer())
                .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
                .create();

        Gson deserializer = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Task.class, new TaskDeserializer())
                .registerTypeAdapter(Epic.class, new EpicDeserializer(fileManager))
                .registerTypeAdapter(Subtask.class, new SubtaskDeserializer(fileManager))
                .create();

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        String response = "";
        int rCode = 500;
        switch (method) {
            case "GET":
                    //получение всего
                if (Pattern.matches("^/tasks$", path)) {
                    response = serializer.toJson(fileManager.getPrioritizedTasks());
                    rCode = 200;
                } else if (Pattern.matches("^/tasks/task$", path) && exchange.getRequestURI().getQuery() != null) {
                    int id = getId(exchange.getRequestURI().getQuery());
                    response = serializer.toJson(fileManager.getTaskById(id));
                    rCode = 200;
                    //получение всех экземпляров
                } else if (Pattern.matches("^/tasks/task$", path)) {
                    response = serializer.toJson(fileManager.getAllTasks());
                    rCode = 200;
                } else if (Pattern.matches("^/tasks/epic$", path)) {
                    response = serializer.toJson(fileManager.getAllEpic());
                    rCode = 200;
                } else if (Pattern.matches("^/tasks/subtask$", path)) {
                    response = serializer.toJson(fileManager.getAllSubtask());
                    rCode = 200;
                    //получение по id
                } else if (Pattern.matches("^/tasks/epic$", path) && exchange.getRequestURI().getQuery() != null) {
                    int epicId = getId(exchange.getRequestURI().getQuery());
                    response = serializer.toJson(fileManager.getEpicById(epicId));
                    rCode = 200;
                } else if (Pattern.matches("^/tasks/subtask$", path) && exchange.getRequestURI().getQuery() != null) {
                    int subtaskId = getId(exchange.getRequestURI().getQuery());
                    response = serializer.toJson(fileManager.getSubtaskById(subtaskId));
                    rCode = 200;
                } else if (Pattern.matches("^/tasks/subtask/epic$", path) && exchange.getRequestURI().getQuery() != null) {
                    int epicId = getId(exchange.getRequestURI().getQuery());
                    response = serializer.toJson(fileManager.getEpicById(epicId).getEpicSubtasks());
                    rCode = 200;
                    //получение истории
                } else if (Pattern.matches("^/tasks/history$", path)) {
                    response = serializer.toJson(fileManager.getHistory());
                    rCode = 200;
                }
                break;

            case "DELETE":
                    //удаление всех экземпляров типа
                if (Pattern.matches("^/tasks/task$", path)) {
                    fileManager.deleteAllTask();
                    response = serializer.toJson("Все задачи типа TASK удалены");
                    rCode = 200;
                } else if (Pattern.matches("^/tasks/epic$", path)) {
                    fileManager.deleteAllEpic();
                    response = serializer.toJson("Все задачи типа EPIC удалены");
                    rCode = 200;
                } else if (Pattern.matches("^/tasks/subtask$", path)) {
                    fileManager.deleteAllSubtask();
                    response = serializer.toJson("Все задачи типа Subtask удалены");
                    rCode = 200;
                    //удаление по id
                } else if (Pattern.matches("^/tasks/task$", path) && exchange.getRequestURI().getQuery() != null) {
                    int id = getId(exchange.getRequestURI().getQuery());
                    fileManager.deleteTaskById(id);
                    response = serializer.toJson("Удален Task c id:" + id);
                    rCode = 200;
                } else if (Pattern.matches("^/tasks/epic$", path) && exchange.getRequestURI().getQuery() != null) {
                    int id = getId(exchange.getRequestURI().getQuery());
                    fileManager.deleteEpicById(id);
                    response = serializer.toJson("Удален Epic c id:" + id);
                    rCode = 200;
                } else if (Pattern.matches("^/tasks/subtask$", path) && exchange.getRequestURI().getQuery() != null) {
                    int id = getId(exchange.getRequestURI().getQuery());
                    fileManager.deleteSubtaskById(id); //
                    response = serializer.toJson("Удален Subtask c id:" + id);
                    rCode = 200;
                }
                break;
                //добавление экземпляров
            case "POST":
                if (Pattern.matches("^/tasks/task$", path)) {
                    InputStream inputStream = exchange.getRequestBody();
                    String taskBody = new String(inputStream.readAllBytes(), Charset.defaultCharset());
                    Task task = deserializer.fromJson(taskBody, Task.class);
                    fileManager.addTask(task);
                    rCode = 201; //лучше использовать код 201 тк создается экземпляр
                } else if (Pattern.matches("^/tasks/epic$", path)) {
                    InputStream inputStream = exchange.getRequestBody();
                    String taskBody = new String(inputStream.readAllBytes(), Charset.defaultCharset());
                    Epic epic = deserializer.fromJson(taskBody, Epic.class);
                    fileManager.addEpic(epic);
                    rCode = 201;
                } else if (Pattern.matches("^/tasks/subtask$", path)) {
                    InputStream inputStream = exchange.getRequestBody();
                    String taskBody = new String(inputStream.readAllBytes(), Charset.defaultCharset());
                    Subtask subtask = deserializer.fromJson(taskBody, Subtask.class);
                    fileManager.addSubtask(subtask);
                    rCode = 201;
                }
                break;
        }
        exchange.sendResponseHeaders(rCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private static int getId(String str) {
        String[] queryArray = str.split("=");
        return Integer.parseInt(queryArray[1]);
    }
}
