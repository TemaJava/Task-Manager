package manager;

import entity.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StringToScvFormatter {
    public static String historyToString(HistoryManager historyManager) {
        List<String> allId = new ArrayList<>();
        for (Task entry : historyManager.getHistory()) {
            allId.add(String.valueOf(entry.getId()));
        }
        return String.join(",", allId);
    }
    public static Task taskFromString(String line) {
        String[] info = line.split(",");
        int id = Integer.parseInt(info[0]);
        String name = info[1];
        String description = info[3];
        TaskStatus taskStatus = getStatusFromString(info[4]);
        TaskType taskType = getTaskTypeFromString(info[2]);
        LocalDateTime startTime = LocalDateTime.parse(info[5]);
        int duration = Integer.parseInt(info[6]);
        Task task = new Task(name, description, taskType, taskStatus, startTime, duration);
        task.setId(id);
        task.setStatus(taskStatus);
        task.setStartTime(startTime);
        task.setDuration(duration);
        return task;
    }

    public static Epic epicFromString(String line) {
        String[] info = line.split(",");
        int id = Integer.parseInt(info[0]);
        String name = info[1];
        String description = info[3];
        TaskStatus taskStatus = getStatusFromString(info[4]);
        TaskType taskType = getTaskTypeFromString(info[2]);
        LocalDateTime startTime = LocalDateTime.parse(info[5]);
        int duration = Integer.parseInt(info[6]);
        Epic epic = new Epic(name, description, taskType, taskStatus, startTime, duration);
        epic.setId(id);
        epic.setStatus(taskStatus);
        epic.setStartTime(startTime);
        epic.setDuration(duration);
        return epic;
    }

    public static Subtask subtaskFromString(String line) {
        String[] info = line.split(",");
        int id = Integer.parseInt(info[0]);
        int epicId = Integer.parseInt(info[7]);
        String name = info[1];
        String description = info[3];
        TaskStatus taskStatus = getStatusFromString(info[4]);
        TaskType taskType = getTaskTypeFromString(info[2]);
        LocalDateTime startTime = LocalDateTime.parse(info[5]);
        int duration = Integer.parseInt(info[6]);

        Subtask subtask = new Subtask(name, description, taskType, taskStatus, epicId, startTime, duration);
        subtask.setId(id);
        subtask.setStatus(taskStatus);
        subtask.setStartTime(startTime);
        subtask.setDuration(duration);
        return subtask;
    }

    public static List<Integer> historyFromString(String str) {
        List<Integer> history = new ArrayList<>();
        String[] nums = str.split(",");

        for (String num : nums) {
            history.add(Integer.parseInt(num));
        }
        return history;
    }

    public static TaskType getTaskTypeFromString(String str) {
        switch (str) {
            case "TASK" -> {
                return TaskType.TASK;
            }
            case "EPIC" -> {
                return TaskType.EPIC;
            }
            case "SUBTASK" -> {
                return TaskType.SUBTASK;
            }
            default -> {
                return null;
            }
        }
    }

    public static TaskStatus getStatusFromString(String str) {
        switch (str) {
            case "NEW" -> {
                return TaskStatus.NEW;
            }
            case "IN_PROGRESS" -> {
                return TaskStatus.IN_PROGRESS;
            }
            case "DONE" -> {
                return TaskStatus.DONE;
            }
            default -> {
                return null;
            }
        }
    }
}
