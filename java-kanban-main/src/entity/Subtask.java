package entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task{
    private int epicId; //id эпика, которому принадлежит задача

    public Subtask(String name, String description, TaskType taskType, TaskStatus status, int EpicId, LocalDateTime startTime,
                   int duration) {
        super(name, description, taskType, status, startTime, duration);
        this.epicId = EpicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return getId() + "," +
                        getName() + ','  + getType() + ',' + getDescription() + ',' +
                        getStatus() + ',' + getStartTime() + "," + getDuration() + "," + getEpicId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask status = (Subtask) o;
        return epicId == status.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}

