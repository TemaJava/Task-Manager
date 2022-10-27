import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.*;
import exceptions.ManagerSaveException;
import http.HttpTaskServer;
import http.KVServer;
import http.KVTaskClient;
import http.LocalDateAdapter;
import http.serializers.TaskSerializer;
import manager.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static entity.TaskStatus.DONE;
import static entity.TaskStatus.IN_PROGRESS;

public class Main {

    public static void main(String[] args) throws ManagerSaveException, IOException, URISyntaxException, InterruptedException {
        System.out.println("Поехали!");

        Task task1 = new Task("Забрать книги", "Поехать в пункт выдачи", TaskType.TASK, TaskStatus.NEW,
                LocalDateTime.of(2020, 7, 11, 14, 0), 50);
        Task task2 = new Task("Выйти из дома", "открыть дверь", TaskType.TASK, TaskStatus.NEW,
                LocalDateTime.of(2019, 4, 5, 10, 30), 30);
        Epic epic1 = new Epic("Одеться", "Почему бы и не одеться", TaskType.EPIC, TaskStatus.NEW);
        Epic epic2 = new Epic("Почитать", "Получаем знания", TaskType.EPIC, TaskStatus.NEW);
        Subtask subtask1 = new Subtask("Открыть книгу", "Резко с прищуром", TaskType.SUBTASK,
                TaskStatus.NEW, 1, LocalDateTime.of(2020, 11, 7, 10, 0), 60);
        Subtask subtask2 = new Subtask("Читаем", "смотрим на страницы", TaskType.SUBTASK, TaskStatus.DONE, 1,
                LocalDateTime.of(2020, 11, 7, 10, 0), 60);
        Subtask subtask3 = new Subtask("Надеть носки", "Быстрым движением", TaskType.SUBTASK,
                TaskStatus.DONE, 3, LocalDateTime.of(2023, 11, 10, 12, 30), 100);

/*
        fileBackedTasksManager.addTask(task2);
        fileBackedTasksManager.addTask(task1);
        fileBackedTasksManager.addEpic(epic1);
        fileBackedTasksManager.addEpic(epic2);
        fileBackedTasksManager.addSubtask(subtask1);
        fileBackedTasksManager.addSubtask(subtask2);
        fileBackedTasksManager.addSubtask(subtask3);


        fileBackedTasksManager.getTaskById(1);
        fileBackedTasksManager.getSubtaskById(5);
        fileBackedTasksManager.getEpicById(4);
        System.out.println(fileBackedTasksManager.getAllTasks());
        System.out.println(fileBackedTasksManager.getAllEpicSubtask(fileBackedTasksManager.getEpicById(4)));

        System.out.println(fileBackedTasksManager.getAllTasks());
        FileBackedTasksManager fileBackedTasksManager1 = FileBackedTasksManager.load(new File("save.csv"));
        Task task3 = new Task("Убраться", "Помыть полы", TaskType.TASK, TaskStatus.NEW,
                LocalDateTime.of(2019, 6, 18, 18, 30), 80);
        fileBackedTasksManager1.addTask(task3);
        fileBackedTasksManager1.getTaskById(8);
        fileBackedTasksManager1.getSubtaskById(5);
        fileBackedTasksManager1.getEpicById(3);
        System.out.println(fileBackedTasksManager1.getPrioritizedTasks());

        System.out.println(fileBackedTasksManager1.getPrioritizedTasks());
        */

        new KVServer().start();
        HttpTaskManager httpTaskManager = (HttpTaskManager) Managers.getDefault(new URI("http://localhost:8078"));
        HttpTaskServer httpTaskServer = new HttpTaskServer(httpTaskManager);
        HttpTaskManager httpTaskManager1 = (HttpTaskManager) Managers.getDefault(new URI("http://localhost:8078"));

        httpTaskManager.addEpic(epic1);
        httpTaskManager.addSubtask(subtask1);
        httpTaskManager.addSubtask(subtask2);
    }
}
//http://localhost:8078/load/epics?API_TOKEN=1666270753598