package tests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskManagerTest {
    KVServer kvServer;
    HttpTaskManager httpTaskManager;
    HttpTaskServer taskServer;
    Gson gson;
    private final static URI  httpServerUri = URI.create("http://localhost:8040/tasks/");
    private final static URI  ksvServerUri = URI.create("http://localhost:8050/tasks/");

    @BeforeEach
    public void beforeEach() {
        try {
            this.kvServer = new KVServer();
            kvServer.start();
            this.httpTaskManager = (HttpTaskManager) Managers.getDefault(URI.create("http://localhost:8050"));
            this.taskServer = new HttpTaskServer(httpTaskManager);
            gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter()).create();
        } catch (IOException | URISyntaxException exception) {
            System.out.println("Ошибка при создании менеджера");
        }
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
    }

    @Test
    public void mustLoadAllTypesOfTaskFromServer() throws IOException, InterruptedException, URISyntaxException {
        Task task = createTestTask();
        task.setStartTime(LocalDateTime.now().minusMonths(10));
        HttpClient httpClient = HttpClient.newHttpClient();
        String request = gson.toJson(task);
        HttpRequest taskCreateRequest = HttpRequest.newBuilder().uri(httpServerUri)
                .POST(HttpRequest.BodyPublishers.ofString(request))
                .build();
        httpClient.send(taskCreateRequest, HttpResponse.BodyHandlers.ofString());

        Epic epic = createTestEpic();
        String requestEpic = gson.toJson(epic);
        HttpRequest epicCreateRequest = HttpRequest.newBuilder().uri(httpServerUri)
                .POST(HttpRequest.BodyPublishers.ofString(requestEpic))
                .build();
        httpClient.send(epicCreateRequest, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = createTestSubtask();
        subtask.setEpicId(2);
        subtask.setStartTime(LocalDateTime.now().minusMonths(8));
        String requestSub = gson.toJson(subtask);
        HttpRequest subtaskCreateRequest = HttpRequest.newBuilder().uri(httpServerUri)
                .POST(HttpRequest.BodyPublishers.ofString(requestSub))
                .build();
        httpClient.send(subtaskCreateRequest, HttpResponse.BodyHandlers.ofString());

        httpTaskManager.load();




    }

    public Task createTestTask() {
        return new Task("testTaskName", "testTaskDesc", TaskType.TASK, TaskStatus.NEW,
                LocalDateTime.now(), 50);
    }

    public Subtask createTestSubtask() {
        return new Subtask("testSubtaskName", "testSubtaskDesc", TaskType.SUBTASK, TaskStatus.NEW,
                0, LocalDateTime.now(), 50);
    }

    public Epic createTestEpic() {
        return new Epic("testEpicName", "testEpicDesc", TaskType.EPIC, TaskStatus.NEW,
                LocalDateTime.now(), 50);
    }


}

