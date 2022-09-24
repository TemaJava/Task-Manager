package manager;

import entity.Task;

public class Node {
    Task task;
    Node prev;
    Node next;

    public Node(Node prev, Node next, Task task) {
        this.prev = prev;
        this.next = next;
        this.task = task;
    }
}
