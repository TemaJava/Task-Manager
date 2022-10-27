package tests;

import entity.Task;
import entity.TaskStatus;
import entity.TaskType;
import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class InMemoryHistoryManagerTest {
    private int id;     //так как в самостоятельном менеджере истории отсутствует ид, создаем его отдельно
    private HistoryManager historyManager;

    public Task createTask() {
        Task task = new Task("Name", "Description", TaskType.TASK, TaskStatus.NEW, LocalDateTime.now(),
                60);
        task.setId(++id);
        return task;
    }

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        id = 0;
    }

    @Test
    public void mustAddTaskToHistory() {
        Task task = createTask();
        historyManager.add(task);
        Assertions.assertEquals(List.of(task), historyManager.getHistory());
    }

    @Test
    public void dontCreateDuplicateInHistory() {
        Task task2 = createTask();
        Task task1 = createTask();
        task1.setStartTime(task1.getStartTime().minusMonths(10));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task2);
        Assertions.assertEquals(List.of(task1, task2), historyManager.getHistory());
    }

    @Test
    public void mustRemoveTaskFromHistoryStart() {
        Task task = createTask();
        historyManager.add(task);
        historyManager.remove(task.getId());
        Assertions.assertEquals(Collections.EMPTY_LIST, historyManager.getHistory());
    }

    @Test
    public void mustRemoveTaskFromHistoryEnd() {
        Task task2 = createTask();
        Task task1 = createTask();
        task1.setStartTime(task1.getStartTime().minusMonths(10));
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task2.getId());
        Assertions.assertEquals(List.of(task1), historyManager.getHistory());
    }

    @Test
    public void mustRemoveTaskFromHistoryMiddle() {
        Task task1 = createTask();
        Task task2 = createTask();
        Task task3 = createTask();
        task1.setStartTime(task1.getStartTime().minusMonths(10));
        task3.setStartTime(task3.getStartTime().minusMonths(20));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());
        Assertions.assertEquals(List.of(task1, task3), historyManager.getHistory());
    }

    @Test
    public void dontRemoveTaskByWrongId() {
        Task task = createTask();
        historyManager.add(task);
        historyManager.remove(10);
        Assertions.assertEquals(List.of(task), historyManager.getHistory());
    }

    @Test
    public void mustMoveTaskToEndOfHistoryIfAlreadyContainsIt() {
        Task task1 = createTask();
        Task task2 = createTask();
        Task task3 = createTask();
        task1.setStartTime(task1.getStartTime().minusMonths(10));
        task3.setStartTime(task3.getStartTime().minusMonths(20));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task2);
        Assertions.assertEquals(List.of(task1, task3, task2), historyManager.getHistory());
    }
}
