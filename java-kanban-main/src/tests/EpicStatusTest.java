package tests;

import entity.*;
import org.junit.jupiter.api.Test;
import manager.InMemoryTaskManager;

import org.junit.jupiter.api.*;

import java.time.LocalDateTime;


public class EpicStatusTest {
    InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    public void createManagers() {
        inMemoryTaskManager = new InMemoryTaskManager();
    }

    @Test
    public void EpicWithoutSubtasksMustBeNew() {
        Epic epic = new Epic("TestName", "TestDescription", TaskType.EPIC, TaskStatus.NEW);
        inMemoryTaskManager.addEpic(epic);
        epic.chooseEpicStatus();

        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void EpicWithAllNewSubtasksMustBeNew() {
        Epic epic = new Epic("TestName", "TestDescription", TaskType.EPIC, TaskStatus.NEW);
        Subtask subtask1 = new Subtask("TestName", "testDescription", TaskType.SUBTASK, TaskStatus.NEW,
                1, LocalDateTime.now(), 60);
        Subtask subtask2 = new Subtask("TestName", "testDescription", TaskType.SUBTASK, TaskStatus.NEW,
                1, LocalDateTime.now(), 60);
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.addSubtask(subtask2);

        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void EpicWithAllDoneSubtasksMustBeDone() {
        Epic epic = new Epic("TestName", "TestDescription", TaskType.EPIC, TaskStatus.NEW);
        Subtask subtask1 = new Subtask("TestName", "testDescription", TaskType.SUBTASK, TaskStatus.DONE,
                1, LocalDateTime.now(), 60);
        Subtask subtask2 = new Subtask("TestName", "testDescription", TaskType.SUBTASK, TaskStatus.DONE,
                1, LocalDateTime.now(), 60);
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.addSubtask(subtask2);

        Assertions.assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void EpicWickDifferentSubtaskStatusMustBeInProgress() {
        Epic epic = new Epic("TestName", "TestDescription", TaskType.EPIC, TaskStatus.NEW);
        Subtask subtask1 = new Subtask("TestName", "testDescription", TaskType.SUBTASK, TaskStatus.DONE,
                1, LocalDateTime.now(), 60);
        Subtask subtask2 = new Subtask("TestName", "testDescription", TaskType.SUBTASK, TaskStatus.NEW,
                1, LocalDateTime.now(), 60);
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.addSubtask(subtask2);

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void EpicWithInProgressSubtaskMustBeInProgress() {
        Epic epic = new Epic("TestName", "TestDescription", TaskType.EPIC, TaskStatus.NEW);
        Subtask subtask1 = new Subtask("TestName", "testDescription", TaskType.SUBTASK,
                TaskStatus.IN_PROGRESS, 1, LocalDateTime.now(), 60);
        Subtask subtask2 = new Subtask("TestName", "testDescription", TaskType.SUBTASK,
                TaskStatus.IN_PROGRESS, 1, LocalDateTime.now(), 60);
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.addSubtask(subtask2);

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }
}
