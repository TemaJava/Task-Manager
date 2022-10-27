package manager;

import com.google.gson.JsonElement;
import entity.Epic;
import entity.Subtask;
import entity.Task;
import exceptions.ManagerSaveException;

import java.util.List;
import java.util.Set;

public interface TaskManager {
    int addTask(Task task) throws ManagerSaveException;
    int addEpic(Epic epic) throws ManagerSaveException;
    int addSubtask(Subtask status) throws ManagerSaveException;
    List<Task> getAllTasks() throws ManagerSaveException;
    List<Epic> getAllEpic() throws ManagerSaveException;
    List<Subtask> getAllSubtask() throws ManagerSaveException;
    Task getTaskById(int id) throws ManagerSaveException;
    Epic getEpicById(int id) throws ManagerSaveException;
    Subtask getSubtaskById(int id) throws ManagerSaveException;
    List<Subtask> getAllEpicSubtask(Epic epic) throws ManagerSaveException;
    void deleteTaskById(int id) throws ManagerSaveException;
    void deleteEpicById(int id) throws ManagerSaveException;
    void deleteSubtaskById(int id) throws ManagerSaveException;
    void deleteAllTask() throws ManagerSaveException;
    void deleteAllEpic() throws ManagerSaveException;
    void deleteAllSubtask() throws ManagerSaveException;
    void updateTask(Task newTask) throws ManagerSaveException;
    void updateEpic(Epic newEpic) throws ManagerSaveException;
    void updateSubtask(Subtask newStatus) throws ManagerSaveException;
    List<Task> getHistory() throws ManagerSaveException;

    public Set<Task> getPrioritizedTasks();

    void load();
}
