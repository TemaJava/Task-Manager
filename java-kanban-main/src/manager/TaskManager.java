package manager;

import entity.Epic;
import entity.Subtask;
import entity.Task;
import java.util.List;

public interface TaskManager {
    int addTask(Task task);
    int addEpic(Epic epic);
    int addSubtask(Subtask status);
    List<Task> getAllTasks();
    List<Epic> getAllEpic();
    List<Subtask> getAllSubtask();
    Task getTaskById(int id);
    Epic getEpicById(int id);
    Subtask getSubtaskById(int id);
    List<Subtask> getAllEpicSubtask(Epic epic);
    void deleteTaskById(int id);
    void deleteEpicById(int id);
    void deleteSubtaskById(int id);
    void deleteAllTask();
    void deleteAllEpic();
    void deleteAllSubtask();
    void updateTask(Task newTask);
    void updateEpic(Epic newEpic);
    void updateSubtask(Subtask newStatus);
    List<Task> getHistory();
}
