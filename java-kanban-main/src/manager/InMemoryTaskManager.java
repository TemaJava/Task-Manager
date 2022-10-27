package manager;

import entity.Epic;
import entity.Subtask;
import entity.Task;
import entity.TaskType;
import exceptions.ManagerStartTimeException;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> taskMap;
    protected Map<Integer, Epic> epicMap;
    protected Map<Integer, Subtask> subtaskMap;
    protected int generatorId;
    protected HistoryManager historyManager; //при создании менеджера, создается история
    protected Set<Task> startTimeSet;

    public InMemoryTaskManager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subtaskMap = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        startTimeSet = new TreeSet<>((o1, o2) -> {
            if (o1.getStartTime().isAfter(o2.getStartTime())) {
                return 1;
            } else if (o1.getStartTime().isBefore(o2.getStartTime())) {
                return -1;
            } else return 0;
        });
    }

    public Task findTask(int id) {
        if (taskMap.containsKey(id)) {
            return taskMap.get(id);
        } else if (epicMap.containsKey(id)) {
            return epicMap.get(id);
        } else return subtaskMap.getOrDefault(id, null);
    }

    public void setGeneratorId(int id) {
        this.generatorId = id;
    }
    //методы добавления
    @Override
    public int addTask(Task task) {
        generatorId++;
        task.setType(TaskType.TASK);
        taskMap.put(generatorId, task);
        task.setId(generatorId);
        addToPrioritySet(task);
        validationCheck();
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        generatorId++;
        epic.setType(TaskType.EPIC);
        epicMap.put(generatorId, epic);
        epic.setId(generatorId);
        addToPrioritySet(epic);
        return epic.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        generatorId++;
        subtask.setType(TaskType.SUBTASK);
        subtaskMap.put(generatorId, subtask);
        subtask.setId(generatorId);
        epicMap.get(subtask.getEpicId()).addSubtask(subtask); //добавляем сабтакс в список сабтасков эпика
        epicMap.get(subtask.getEpicId()).chooseEpicStatus(); //проверяем статус эпика
        epicMap.get(subtask.getEpicId()).updateEpicEndTime(); //обновляем поля времени эпика
        addToPrioritySet(subtask);
        validationCheck();
        return subtask.getId();
    }

    //методы удаления всех задач/эпиков/сабтасков
    @Override
    public void deleteAllTask() {        //метод удаления всех задач
        for (Integer id:taskMap.keySet()) {
            historyManager.remove(id);
            startTimeSet.remove(taskMap.get(id));
        }
        taskMap.clear();

    }

    @Override
    public void deleteAllEpic() {              //метод удаления всех эпиков
        for (Integer id:subtaskMap.keySet()) {
            historyManager.remove(id);
            startTimeSet.remove(subtaskMap.get(id));
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
            startTimeSet.remove(subtaskMap.get(id));
        }
        subtaskMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.getEpicSubtasks().clear();
            epic.chooseEpicStatus(); //все сабтаски удалены. По тз эпик без сабтасков имеет значение статуса NEW
            epic.updateEpicEndTime();
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
        validationCheck();
    }

    @Override
    public void updateEpic(Epic newEpic) {
        int id = newEpic.getId();
        Epic savedEpic = epicMap.get(id);
        if (savedEpic == null) {
            return;
        }
        newEpic.chooseEpicStatus(); //проверка статуса эпика перед обновлением
        newEpic.updateEpicEndTime();
        epicMap.put(id, newEpic);
        validationCheck();
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        int id = newSubtask.getId();
        Subtask savedStatus = subtaskMap.get(id);
        if (savedStatus == null) {
            return;
        }
        subtaskMap.put(id, newSubtask);
        epicMap.get(newSubtask.getEpicId()).chooseEpicStatus(); //проверка статуса эпика после добавления сабтаска
        epicMap.get(newSubtask.getEpicId()).updateEpicEndTime();
        validationCheck();
    }

    //методы удаления по идентификатору
    @Override
    public void deleteTaskById(int id) {
        if (taskMap.containsKey(id)) {
            historyManager.remove(id);
            startTimeSet.remove(taskMap.get(id));
            if (taskMap.get(id) != null) {
                taskMap.remove(id);
            }
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epicMap.containsKey(id)) {
            Map<Integer, Subtask> newMap = new HashMap<>();
            historyManager.remove(id);
            if (epicMap.get(id) != null) {
                for (Subtask subtask : subtaskMap.values()) {
                    historyManager.remove(subtask.getId());
                    startTimeSet.remove(subtaskMap.get(id));
                    if (subtask.getEpicId() != id) {               //при удалении эпика удаляем и сабтаски
                        newMap.put(subtask.getId(), subtask); //для этого создаем новую HahMap, но уже без старых сабтасков
                    }
                }
                subtaskMap = newMap;
                epicMap.remove(id);
            }
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtaskMap.get(id) != null) {
            historyManager.remove(id);
            startTimeSet.remove(subtaskMap.get(id));
            //удаляем сабтакс из списка в эпике
            epicMap.get(subtaskMap.get(id).getEpicId()).getEpicSubtasks().remove(subtaskMap.get(id));
            //проверяем статус эпика после удаления сабтаска
            if (epicMap.get(subtaskMap.get(id).getEpicId()).getEpicSubtasks() != null) {
                epicMap.get(subtaskMap.get(id).getEpicId()).chooseEpicStatus();
                epicMap.get(subtaskMap.get(id).getEpicId()).updateEpicEndTime();
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
            historyManager.add(task);
            taskList.add(task);
        }
        return taskList;
    }

    @Override
    public List<Epic> getAllEpic() {
        List<Epic> epics = new ArrayList<>();
        for (Epic epic : epicMap.values()) {
            historyManager.add(epic);
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
            historyManager.add(subtask);
            subtasks.add(subtask);
        }
        return subtasks;
    }

    @Override
    public List<Task> getHistory() { //метод, возвращающий историю просмотра
        return historyManager.getHistory();
    }

    public void validationCheck() {
        List<Task> list = new ArrayList<>(startTimeSet);
        for (int i = 1; i < list.size(); i++) {
            Task task1 = list.get(i);
            boolean flag = task1.getStartTime().isBefore(list.get(i-1).getEndTime());
            if (flag) {
                throw new ManagerStartTimeException("Обнаружено пересечение задач. ID: " + task1.getId() + ", " +
                        list.get(i-1).getId());
            }
        }
    }
    public Set<Task> getPrioritizedTasks() {
        startTimeSet.addAll(taskMap.values());
        startTimeSet.addAll(subtaskMap.values());
        return startTimeSet;
    }

    @Override
    public void load() {

    }

    public void addToPrioritySet(Task task) {
        startTimeSet.add(task);
    }
}
