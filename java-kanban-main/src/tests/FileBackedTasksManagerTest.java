package tests;

import entity.Epic;
import entity.Subtask;
import entity.Task;
import manager.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class FileBackedTasksManagerTest extends TaskManagerTest {
    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTasksManager(new File("save.test.csv"));
    }

    @AfterEach                          //без удаления файла столкнемся с ошибкой пересечения
    public void afterEach() {
        try {
            Files.delete(Path.of("save.test.csv"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void mustAccuratelyLoadHistoryInNormalCondition() {
        Task task = createTask();
        task.setStartTime(task.getStartTime().minusMonths(10));
        taskManager.addTask(task);
        Epic epic = createEpic();
        taskManager.addEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addSubtask(subtask);
        taskManager.getAllTasks();
        taskManager.getEpicById(2);

        FileBackedTasksManager.load(new File("save.test.csv"));

        Assertions.assertEquals(List.of(task), taskManager.getAllTasks());
        Assertions.assertEquals(List.of(epic, task), taskManager.getHistory());
    }

    @Test
    public void mustAccuratelyLoadHistoryWithEmptyHistory() {
        Task task = createTask();
        task.setStartTime(task.getStartTime().minusMonths(10));
        taskManager.addTask(task);
        Epic epic = createEpic();
        taskManager.addEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addSubtask(subtask);

        FileBackedTasksManager.load(new File("save.test.csv"));
        Assertions.assertEquals(Collections.EMPTY_LIST, taskManager.getHistory());
        Assertions.assertEquals(List.of(task), taskManager.getAllTasks());
    }

    @Test
    public void epicWithoutSubtasksInHistory() {
        Epic epic = createEpic();
        taskManager.addEpic(epic);
        taskManager.getEpicById(1);

        FileBackedTasksManager.load(new File("save.test.csv"));
        Assertions.assertEquals(List.of(epic), taskManager.getAllEpic());
        Assertions.assertEquals(List.of(epic), taskManager.getHistory());
    }
}
