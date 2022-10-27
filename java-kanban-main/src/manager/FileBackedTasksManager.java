package manager;

import entity.Epic;
import entity.Subtask;
import entity.Task;
import entity.TaskType;
import exceptions.ManagerSaveException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTasksManager (File file) {
        super();
        this.file = file;
    }

    public static FileBackedTasksManager load(File file) throws ManagerSaveException {
        FileBackedTasksManager tasksManager = new FileBackedTasksManager(file);

        try {
            String csv = Files.readString(file.toPath());
            String[] lines = csv.split("\n"); //разбиваем файл на строки
            List<Integer> history = Collections.emptyList();
            int generatorId = 0;

            for (int i = 1; i < lines.length; i++) {  //считываем таски
                String line = lines[i];
                if (line.isEmpty()) { //пустая строка, значит, дошли до истории
                        history = StringToScvFormatter.historyFromString(lines[i + 1]);
                        break;
                }

                String[] taskInfo = line.split(",");
                TaskType currentTaskType = StringToScvFormatter.getTaskTypeFromString(taskInfo[2]);
                switch (Objects.requireNonNull(currentTaskType)) {
                    case TASK -> {
                        Task task = StringToScvFormatter.taskFromString(lines[i]);
                        tasksManager.taskMap.put(task.getId(), task);
                        //проверка для перезаписи айди
                        if (generatorId < task.getId()) {
                            tasksManager.setGeneratorId(task.getId());
                            generatorId = task.getId();
                        }
                    }

                    case EPIC -> {
                        Epic epic = StringToScvFormatter.epicFromString(lines[i]);
                        tasksManager.epicMap.put(epic.getId(), epic);
                        if (generatorId < epic.getId()) {
                            tasksManager.setGeneratorId(epic.getId());
                            generatorId = epic.getId();
                        }
                    }
                    case SUBTASK -> {
                        Subtask subtask = StringToScvFormatter.subtaskFromString(lines[i]);
                        tasksManager.subtaskMap.put(subtask.getId(), subtask);
                        if (generatorId < subtask.getId()) {
                            tasksManager.setGeneratorId(subtask.getId());
                            generatorId = subtask.getId();
                        }
                    }
                }
            }
            tasksManager.generatorId = generatorId;
            //восстановление истории
            if (!history.isEmpty()) {
                for (Integer id : history) {
                    tasksManager.historyManager.add(tasksManager.findTask(id));
                }
            }
            //восстанавливаем айди сабтасков у эпиков
            for (Map.Entry<Integer, Subtask> entry : tasksManager.subtaskMap.entrySet()) {
                Subtask subtask = entry.getValue();
                Epic epic = tasksManager.epicMap.get(subtask.getEpicId());
                epic.addSubtask(subtask);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("не удалось прочитать файл - " + file);
        }
        return tasksManager;
    }

    public void save() {
        File fileToSave = new File(String.valueOf(file.toPath()));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))){
            writer.write("id,name,type,description,status,startTime,duration,epic\n");
            //записываем всю информацию из мап
            for (Map.Entry<Integer, Task> task : taskMap.entrySet()) {
                Task taskInfo = task.getValue();
                writer.write(taskInfo.toString() + "\n");
            }
            for (Map.Entry<Integer, Epic> epic : epicMap.entrySet()) {
                Epic epicInfo = epic.getValue();
                writer.write(epicInfo.toString() + "\n");
            }
            for (Map.Entry<Integer, Subtask> subtask : subtaskMap.entrySet()) {
                Subtask subtaskInfo = subtask.getValue();
                writer.write(subtaskInfo.toString() + "\n");
            }
            writer.write("\n");
            writer.write(StringToScvFormatter.historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при сохранении файла");
        }
    }

    @Override
    public int addTask(Task task)  {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        super.addEpic(epic);
        int id = epic.getId();
        save();
        return id;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = super.addSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }

    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask newStatus) {
        super.updateSubtask(newStatus);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> list = super.getAllTasks();
        save();
        return list;
    }

    @Override
    public List<Epic> getAllEpic() {
        List<Epic> list = super.getAllEpic();
        save();
        return list;
    }

    @Override
    public List<Subtask> getAllEpicSubtask(Epic epic) {
        List<Subtask> list = super.getAllEpicSubtask(epic);
        save();
        return list;
    }

    @Override
    public List<Subtask> getAllSubtask() {
        List<Subtask> list = super.getAllSubtask();
        save();
        return list;
    }
}
