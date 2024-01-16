package test;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private final static String name = "Project";
    private final static String description = "Do!";
    private static Task task1;
    private static HistoryManager historyManager;


    @BeforeEach
    public void createAllFields() {
        task1 = new Task(name, description);
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldSavePreviousHistory() {
        historyManager.addHistory(task1);
        task1.setName("Music");
        task1.setDescription("Write song");
        Task taskHistory = historyManager.getHistory().get(0);
        assertEquals(name, taskHistory.getName());
        assertEquals(description, taskHistory.getDescription());

    }

    @Test
    void sizeOfHistoryShouldEquals10() {
        for (int i = 0; i <= 15; i++) {
            historyManager.addHistory(task1);
        }
        assertEquals(10, historyManager.getHistory().size());
    }

}