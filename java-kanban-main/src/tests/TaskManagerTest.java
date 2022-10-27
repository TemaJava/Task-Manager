package tests;

import entity.*;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

    public abstract class TaskManagerTest {
    TaskManager taskManager;

    public Task createTask() {
        return new Task("Name", "Description", TaskType.TASK, TaskStatus.NEW, LocalDateTime.now(),
                60);
    }

    public Epic createEpic() {
        return new Epic("Name", "Description", TaskType.EPIC, TaskStatus.NEW);
    }

    public Subtask createSubtask(Epic epic) {
        return new Subtask("Name", "Description", TaskType.SUBTASK, TaskStatus.NEW, epic.getId(),
                LocalDateTime.now(), 60 );
    }

    @Test
    public void mustCreateTask() {
        int id = taskManager.addTask(createTask());
        Task task = taskManager.getTaskById(id);
        List<Task> taskList = taskManager.getAllTasks();

        Assertions.assertEquals(1, taskList.size());
        Assertions.assertEquals(List.of(task), taskList);
        Assertions.assertEquals(TaskStatus.NEW, task.getStatus());
        Assertions.assertNotNull(task.getName());
    }

    @Test
    public void mustCreateEpic() {
        int id = taskManager.addEpic(createEpic());
        Epic epic = taskManager.getEpicById(id);
        List<Epic> epicList = taskManager.getAllEpic();

        Assertions.assertEquals(1, epicList.size());
        Assertions.assertEquals(List.of(epic), epicList);
        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());
        Assertions.assertNotNull(epic.getName());
    }

    @Test
    public void mustCreateSubtask() {
        Epic epic = createEpic();
        taskManager.addEpic(epic);
        int id = taskManager.addSubtask(createSubtask(epic));
        Subtask subtask = taskManager.getSubtaskById(id);
        List<Subtask> subtaskList = taskManager.getAllSubtask();

        Assertions.assertEquals(1, subtaskList.size());
        Assertions.assertEquals(List.of(subtask), subtaskList);
        Assertions.assertEquals(TaskStatus.NEW, subtask.getStatus());
        Assertions.assertNotNull(subtask.getName());
        Assertions.assertEquals(List.of(subtask), epic.getEpicSubtasks());
    }

    @Test
    public void mustDeleteTaskById() {
        Task task = createTask();
        taskManager.addTask(task);
        Assertions.assertEquals(List.of(task), taskManager.getAllTasks());
        taskManager.deleteTaskById(1);
        Assertions.assertNull(taskManager.getTaskById(1));
    }

    @Test
    public void mustDeleteEpicById() {
        Epic epic = createEpic();
        taskManager.addEpic(epic);
        Assertions.assertEquals(List.of(epic), taskManager.getAllEpic());
        taskManager.deleteEpicById(1);
        Assertions.assertNull(taskManager.getEpicById(1));
    }

    @Test
    public void mustDeleteSubtaskById() {
        Epic epic = createEpic();
        taskManager.addEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addSubtask(subtask);
        Assertions.assertEquals(List.of(subtask), taskManager.getAllSubtask());
        taskManager.deleteSubtaskById(1);
        Assertions.assertNull(taskManager.getSubtaskById(1));
    }

    @Test
    public void SubtaskMustHaveEpicId() {
        Epic epic = createEpic();
        taskManager.addEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addSubtask(subtask);
        Assertions.assertEquals(epic.getId(), subtask.getEpicId());
    }

    @Test
    public void dontRemoveTaskIfIdBad() {
        Task task = createTask();
        taskManager.addTask(task);
        taskManager.deleteTaskById(10);
        Assertions.assertEquals(List.of(task), taskManager.getAllTasks());
    }

    @Test
    public void dontRemoveEpicIfIdBad() {
        Epic epic = createEpic();
        taskManager.addEpic(epic);
        taskManager.deleteEpicById(10);
        Assertions.assertEquals(List.of(epic), taskManager.getAllEpic());
    }

    @Test
    public void dontRemoveSubtaskIfIdBad() {
        Epic epic = createEpic();
        taskManager.addEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addSubtask(subtask);
        taskManager.deleteSubtaskById(10);
        Assertions.assertEquals(List.of(subtask), taskManager.getAllSubtask());
    }

    @Test
    public void mustRemoveAllTask() {
        Task task1 = createTask();
        Task task2 = createTask();
        task2.setStartTime(task2.getStartTime().plusDays(10));  //избегание пересечений
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        Assertions.assertEquals(List.of(task1, task2), taskManager.getAllTasks());
        taskManager.deleteAllTask();
        Assertions.assertEquals(Collections.emptyList() ,taskManager.getAllTasks());
    }

    @Test
    public void mustRemoveAllEpic() {
        Epic epic1 = createEpic();
        Epic epic2 = createEpic();
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        epic2.setStartTime(epic2.getStartTime().minusMonths(10));  //избегание пересечений

        Assertions.assertEquals(List.of(epic1, epic2), taskManager.getAllEpic());
        taskManager.deleteAllEpic();
        Assertions.assertEquals(Collections.emptyList() ,taskManager.getAllEpic());
    }

    @Test
    public void mustRemoveAllSubtask() {
        Epic epic1 = createEpic();
        Epic epic2 = createEpic();
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        epic2.setStartTime(epic2.getStartTime().minusMonths(10));
        Subtask subtask1 = createSubtask(epic1);
        Subtask subtask2 = createSubtask(epic2);
        subtask2.setStartTime(subtask2.getStartTime().plusDays(10));  //избегание пересечений
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        Assertions.assertEquals(List.of(subtask1, subtask2), taskManager.getAllSubtask());
        taskManager.deleteAllSubtask();
        Assertions.assertEquals(Collections.emptyList() ,taskManager.getAllSubtask());
    }

    @Test
    public void mustUpdateTask() {
        Task task = createTask();
        taskManager.addTask(task);
        task.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task);

        Assertions.assertEquals(TaskStatus.DONE, taskManager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void mustUpdateEpic() {
        Epic epic = createEpic();
        taskManager.addEpic(epic);
        epic.setDescription("test");
        taskManager.updateEpic(epic);

        Assertions.assertEquals("test", taskManager.getEpicById(epic.getId()).getDescription());
    }

    @Test
    public void mustUpdateSubtask() {
        Epic epic = createEpic();
        taskManager.addEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addSubtask(subtask);
        subtask.setStatus(TaskStatus.DONE);
        taskManager.updateTask(subtask);

        Assertions.assertEquals(TaskStatus.DONE, taskManager.getSubtaskById(subtask.getId()).getStatus());
    }

    @Test
    public void mustGetAllTaskEpicSubtasks() {
        Epic epic1 = createEpic();
        Epic epic2 = createEpic();
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        epic2.setStartTime(epic2.getStartTime().minusMonths(10));
        Subtask subtask1 = createSubtask(epic1);
        Subtask subtask2 = createSubtask(epic2);
        subtask2.setStartTime(subtask2.getStartTime().plusDays(40));  //избегание пересечений
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        Task task1 = createTask();
        Task task2 = createTask();
        task2.setStartTime(task2.getStartTime().plusDays(10));  //избегание пересечений
        task1.setStartTime(task1.getStartTime().plusDays(20));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Assertions.assertEquals(List.of(task1, task2), taskManager.getAllTasks());
        Assertions.assertEquals(List.of(subtask1, subtask2), taskManager.getAllSubtask());
        Assertions.assertEquals(List.of(epic1, epic2), taskManager.getAllEpic());
    }
}