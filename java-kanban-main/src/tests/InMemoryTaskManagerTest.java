package tests;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }
}
