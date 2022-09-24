package manager;

import entity.Epic;
import entity.Subtask;
import entity.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> taskMap;
    private Map<Integer, Epic> epicMap;
    private Map<Integer, Subtask> subtaskMap;
    private int generatorId;
    private final HistoryManager historyManager; //при создании менеджера, создается история

    public InMemoryTaskManager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subtaskMap = new HashMap<>();
        this.generatorId = 0;
        historyManager = Managers.getDefaultHistory();
    }

    //методы добавления
    @Override
    public int addTask(Task task) {
        generatorId++;
        taskMap.put(generatorId, task);
        task.setId(generatorId);
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        generatorId++;
        epicMap.put(generatorId, epic);
        epic.setId(generatorId);
        return epic.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        generatorId++;
        subtaskMap.put(generatorId, subtask);
        subtask.setId(generatorId);
        epicMap.get(subtask.getEpicId()).getEpicSubtasks().add(subtask); //добавляем сабтакс в список сабтасков эпика
        epicMap.get(subtask.getEpicId()).chooseEpicStatus(); //проверяем статус эпика
        return subtask.getId();
    }

    //методы удаления всех задач/эпиков/сабтасков
    @Override
    public void deleteAllTask() {        //метод удаления всех задач
        for (Integer id:taskMap.keySet()) {
            historyManager.remove(id);
        }
        taskMap.clear();
    }

    @Override
    public void deleteAllEpic() {              //метод удаления всех эпиков
        for (Integer id:subtaskMap.keySet()) {
            historyManager.remove(id);
        }
        for (Integer id:epicMap.keySet()) {
            historyManager.remove(id);
        }
        for (Epic epic : epicMap.values()) {     //при удалении всех эпиков удаляются все сабтаски
            int epicId = epic.getId();
            for (Subtask status : subtaskMap.values()) {
                if (status.getEpicId() == epicId) {
                    subtaskMap.remove(status.getId());
                }
            }
        }
        epicMap.clear();
    }

    @Override
    public void deleteAllSubtask() {            //метод удаления всех сабтасков
        for (Integer id:subtaskMap.keySet()) {
            historyManager.remove(id);
        }
        subtaskMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.getEpicSubtasks().clear();
            epic.chooseEpicStatus(); //все сабтаски удалены. По тз эпик без сабтасков имеет значение статуса NEW
        }
    }

    //методы обновления
    @Override
    public void updateTask(Task newTask) {
        int id = newTask.getId();
        Task savedTask = taskMap.get(id);
        if (savedTask == null) {
            return;
        }
        taskMap.put(id, newTask);
    }

    @Override
    public void updateEpic(Epic newEpic) {
        int id = newEpic.getId();
        Epic savedEpic = epicMap.get(id);
        if (savedEpic == null) {
            return;
        }
        newEpic.chooseEpicStatus(); //проверка статуса эпика перед обновлением
        epicMap.put(id, newEpic);
    }

    @Override
    public void updateSubtask(Subtask newStatus) {
        int id = newStatus.getId();
        Subtask savedStatus = subtaskMap.get(id);
        if (savedStatus == null) {
            return;
        }
        subtaskMap.put(id, newStatus);
        epicMap.get(newStatus.getEpicId()).chooseEpicStatus(); //проверка статуса эпика после добавления сабтаска

    }

    //методы удаления по идентификатору
    @Override
    public void deleteTaskById(int id) {
        historyManager.remove(id);
        if (taskMap.get(id) != null) {
            taskMap.remove(id);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Map<Integer, Subtask> newMap = new HashMap<>();
        historyManager.remove(id);
        if (epicMap.get(id) != null) {
            for (Subtask subtask : subtaskMap.values()) {
                historyManager.remove(subtask.getId());
                if (subtask.getEpicId() != id) {               //при удалении эпика удаляем и сабтаски
                    newMap.put(subtask.getId(), subtask); //для этого создаем новую HahMap, но уже без старых сабтасков
                }
            }
            subtaskMap = newMap;
            epicMap.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtaskMap.get(id) != null) {
            historyManager.remove(id);
            //удаляем сабтакс из списка в эпике
            epicMap.get(subtaskMap.get(id).getEpicId()).getEpicSubtasks().remove(subtaskMap.get(id));
            //проверяем статус эпика после удаления сабтаска
            if (epicMap.get(subtaskMap.get(id).getEpicId()).getEpicSubtasks() != null) {
                epicMap.get(subtaskMap.get(id).getEpicId()).chooseEpicStatus();
            }
        }
        subtaskMap.remove(id);
    }

    //методы получения по id
    @Override
    public Task getTaskById(int id) {
        if (taskMap.get(id) != null) {
            historyManager.add(taskMap.get(id)); //функционал добавления задачи в историю
            return taskMap.get(id);
        }
        return null;
    }

    @Override
    public Epic getEpicById(int id) {
        if (epicMap.get(id) != null) {
            historyManager.add(epicMap.get(id)); //функционал добавления эпика в историю
            return epicMap.get(id);
        }
        return null;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtaskMap.get(id) != null) {
            historyManager.add(subtaskMap.get(id)); //функционал добавления сабтаска в историю
            return subtaskMap.get(id);
        }
        return null;
    }

    //методы получения списка всех задач
    @Override
    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        for (Task task : taskMap.values()) {
            taskList.add(task);
        }
        return taskList;
    }

    @Override
    public List<Epic> getAllEpic() {
        List<Epic> epics = new ArrayList<>();
        for (Epic epic : epicMap.values()) {
            epics.add(epic);
        }
        return epics;
    }

    @Override
    public List<Subtask> getAllEpicSubtask(Epic epic) {    //метод для получения всех сабтасков эпика
        return epic.getEpicSubtasks();
    }

    @Override
    public List<Subtask> getAllSubtask() {
        List<Subtask> subtasks = new ArrayList<>();
        for (Subtask subtask : subtaskMap.values()) {
            subtasks.add(subtask);
        }
        return subtasks;
    }

    @Override
    public List<Task> getHistory() { //метод, возвращающий историю просмотра
        return historyManager.getHistory();
    }
}
