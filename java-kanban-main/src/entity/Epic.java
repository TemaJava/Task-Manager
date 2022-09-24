package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Subtask> epicSubtask;  //список сабтасков, принадлежащих эпику

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        this.epicSubtask = new ArrayList<>();
    }

    public List<Subtask> getEpicSubtasks() {
        return epicSubtask;
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
            setStatus(TaskStatus.NEW);
        } else if (doneCounter == epicSubtask.size()) {
            setStatus(TaskStatus.DONE);
        } else {
            setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' + ", epicSubtasks=" + epicSubtask + '\'' +
                '}';
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
    public void setStatus(TaskStatus status) { //метод переопределен, т.к. самостоятельно изменять статус эпика запрещено
    }
}
