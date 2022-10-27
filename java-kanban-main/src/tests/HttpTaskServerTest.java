package tests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import entity.*;
import http.HttpTaskServer;
import http.KVServer;
import http.LocalDateAdapter;
import http.deserializers.EpicDeserializer;
import http.deserializers.SubtaskDeserializer;
import http.deserializers.TaskDeserializer;
import http.serializers.EpicSerializer;
import http.serializers.SubtaskSerializer;
import http.serializers.TaskSerializer;
import manager.HttpTaskManager;
import manager.Managers;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class HttpTaskServerTest {
    private static KVServer kvServer;
    private static HttpTaskServer taskServer;
    private static Gson serializer;

    private static Gson deserializer;
    //запуск и создание серверов
    @BeforeAll
    static void beforeAll() throws IOException, URISyntaxException {
            kvServer = new KVServer();
            kvServer.start();
            HttpTaskManager httpTaskManager = (HttpTaskManager)Managers.getDefault(URI.create("http://localhost:8000"));
            taskServer = new HttpTaskServer(httpTaskManager);
            deserializer = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Task.class, new TaskDeserializer())
                .registerTypeAdapter(Epic.class, new EpicDeserializer(httpTaskManager))
                .registerTypeAdapter(Subtask.class, new SubtaskDeserializer(httpTaskManager))
                .create();
            serializer = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Task.class, new TaskSerializer())
                .registerTypeAdapter(Epic.class, new EpicSerializer())
                .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
                .create();
    }
    //Закрытие серверов
    @AfterAll
    static void afterAll() {
        kvServer.stop();
        taskServer.stop();
    }
    //Перезапуск сервера. Ошибка при стоп-стар -- теряется апи токен. По этой причине будем очищать
    @BeforeEach
    void resetServer() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI urlTask = URI.create("http://localhost:8040/tasks/task/");
            HttpRequest request = HttpRequest.newBuilder().uri(urlTask).DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

            URI urlEpic = URI.create("http://localhost:8040/tasks/epic/");
            request = HttpRequest.newBuilder().uri(urlEpic).DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

            URI urlSub = URI.create("http://localhost:8040/tasks/subtask/");
            request = HttpRequest.newBuilder().uri(urlSub).DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void mustReturnTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8040/tasks/task");
        Task task = createTestTask();
        String json = serializer.toJson(task);
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
        Assertions.assertEquals(1, arrayTasks.size());
    }

    @Test
    public void mustReturnEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8040/tasks/epic");
        Epic epic = createTestEpic();
        String json = serializer.toJson(epic);
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        JsonArray epicArray = JsonParser.parseString(response.body()).getAsJsonArray();
        Assertions.assertEquals(1, epicArray.size());

    }

    @Test
    public void mustReturnSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8040/tasks/subtask");
        Subtask subtask = createTestSubtask();
        String json = serializer.toJson(subtask);
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        JsonArray arraySubtasks = JsonParser.parseString(response.body()).getAsJsonArray();
        Assertions.assertEquals(1, arraySubtasks.size());

    }



    public Task createTestTask() {
        return new Task("testTaskName", "testTaskDesc", TaskType.TASK, TaskStatus.NEW,
                LocalDateTime.now(), 50);
    }

    public Subtask createTestSubtask() {
        return new Subtask("testSubtaskName", "testSubtaskDesc", TaskType.SUBTASK, TaskStatus.NEW,
                1, LocalDateTime.now(), 50);
    }

    public Epic createTestEpic() {
        Epic epic = new Epic("testEpicName", "testEpicDesc", TaskType.EPIC, TaskStatus.NEW,
                LocalDateTime.now(), 50);
        Subtask subtask = createTestSubtask();
        epic.setEpicSubtask(List.of(subtask));
        return epic;
    }

}
