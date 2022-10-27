package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private int id = 0;
    private String name;
    private String description;
    private LocalDateTime startTime = LocalDateTime.MAX;
    private int duration = 0;
    private TaskStatus status; //NEW, IN_PROGRESS, DONE
    private TaskType type; //TASK, EPIC, SUBTASK

    public Task(String name, String description, TaskType taskType, TaskStatus taskStatus, LocalDateTime startTime,
                int duration) {
        this.name = name;
        this.description = description;
        this.type = taskType;
        this.status = taskStatus;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(int minutes) {
        this.duration = minutes;
    }

    public int getDuration() {
        return duration;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType taskType) {
        this.type = taskType;
    }

    public LocalDateTime getEndTime() {
        LocalDateTime endTime;
        return endTime = startTime.plusMinutes(duration);
    }

    @Override
    public String toString() {
        return id + "," + name + "," + type + "," + description + "," + status +
                "," + startTime + "," + duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }
}
