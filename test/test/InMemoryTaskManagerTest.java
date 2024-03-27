package test;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import java.util.HashMap;

 class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void beforeEachTest() {
        taskManager = new InMemoryTaskManager(new HashMap<>(), new HashMap<>(), new HashMap<>(), 0);
        super.beforeEachTest();
    }
}