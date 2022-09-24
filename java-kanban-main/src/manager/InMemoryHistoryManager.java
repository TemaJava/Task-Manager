package manager;

import entity.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node first;
    private Node last;
    private Map<Integer, Node> nodeMap;


    public InMemoryHistoryManager() {
         this.nodeMap = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        removeNode(task.getId());
        Node newNode = linkNew(task);
        nodeMap.put(task.getId(), newNode);
    }

    private Node linkNew(Task task) {
        Node newNode = new Node(last, null, task);
        if (first == null) {
            first = newNode;
        } else {
            last.next = newNode;
        }
        last = newNode;
        return newNode;
    }
    private void removeNode(int id) {
        Node node = nodeMap.remove(id);
        if (node == null) {
            return;
        }
        if (node.prev != null) {     //удаляемая нода не первая
            node.prev.next = node.next;
            if (node.next == null) {  //удаляемая нода последняя
                last = node.prev;
            } else {
                node.next.prev = node.prev;
            }
        } else {
            first = node.next; //удаляемая нода первая
            if (first == null) {     //удаляемая нода была единственной
                last = null;
            } else {
                first.prev = null;
            }
        }
    }

    private List<Task> getTasks() {
        List<Task> newHistory = new ArrayList<>();
        Node currentNode = first;
        while (currentNode != null) {
            newHistory.add(currentNode.task);
            currentNode = currentNode.next;
        }
        return newHistory;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (nodeMap.containsKey(id)) {
            removeNode(id);
            nodeMap.remove(id);
        }
    }
}
