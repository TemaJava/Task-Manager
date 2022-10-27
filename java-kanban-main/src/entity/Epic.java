package entity;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Subtask> epicSubtask = Collections.emptyList();  //список сабтасков, принадлежащих эпику
    private LocalDateTime endTime = LocalDateTime.MIN;
    public Epic(String name, String description, TaskType taskType, TaskStatus taskStatus, LocalDateTime startTime,
                int duration) {
        super(name, description, taskType, taskStatus, startTime, duration);
        this.epicSubtask = new ArrayList<>();
    }

    public Epic(String name, String description, TaskType taskType, TaskStatus taskStatus) {
        super(name, description, taskType, taskStatus, LocalDateTime.MAX, 0);
        this.epicSubtask = new ArrayList<>();
    }

    public void setEpicSubtask(List<Subtask> epicSubtask) {
        this.epicSubtask = epicSubtask;
    }

    public List<Subtask> getEpicSubtasks() {
        return epicSubtask;
    }

    public void addSubtask(Subtask subtask) {
        epicSubtask.add(subtask);
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void chooseEpicStatus() {   //метод для проверки статуса Эпика
        if (epicSubtask.isEmpty()) {
            setStatus(TaskStatus.NEW);
        }
        int newCounter = 0;
        int doneCounter = 0;
        for (Subtask status : epicSubtask) {
            if (Objects.equals(status.getStatus(), TaskStatus.NEW)) {  //подсчет количества сабтаксов с статусами NEW
                newCounter++;
            } else if (Objects.equals(status.getStatus(), TaskStatus.DONE)){
                doneCounter++; //подсчет количества сабтаксов с статусами NEW
            }
        }
        //сравнение количества сабтасков со статусами и общего числа сабтасков эпика
        if (newCounter == epicSubtask.size()) {
            this.setStatus(TaskStatus.NEW);
        } else if (doneCounter == epicSubtask.size()) {
            this.setStatus(TaskStatus.DONE);
        } else {
            this.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public String toString() {
        return  getId() + "," +
                getName() + ',' + getType() + "," + getDescription() + ',' +
                getStatus() + "," + getStartTime() + "," + getDuration();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(epicSubtask, epic.epicSubtask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicSubtask);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void updateEpicEndTime() {
        setDuration(0);
        setStartTime(LocalDateTime.MAX);
        setEndTime(LocalDateTime.MIN);
        for (Subtask subtask:epicSubtask) {
            if (subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
                if (subtask.getStartTime().isBefore(getStartTime())) {
                    setStartTime(subtask.getStartTime());
                }
            setDuration(getDuration() + subtask.getDuration());
        }
    }
}
