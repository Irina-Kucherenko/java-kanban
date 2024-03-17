package test;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;

import java.util.List;

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
        task1.setId(8);
        historyManager.addHistory(task1);
        task1.setName("Music");
        task1.setDescription("Write song");
        Task taskHistory = historyManager.getHistory().get(0);
        assertEquals(name, taskHistory.getName());
        assertEquals(description, taskHistory.getDescription());

    }

    @Test
    void removeTest() {
        task1.setId(1);
        Task task2 = new Task("something", "doing things");
        task2.setId(2);
        Task task3 = new Task("lose weight", "do some sport");
        task3.setId(3);
        historyManager.addHistory(task1);
        historyManager.addHistory(task2);
        historyManager.addHistory(task3);
        historyManager.remove(task2.getId());
        assertEquals(List.of(task1, task3), historyManager.getHistory());

    }

    @Test
    void addHistory() {
        task1.setId(1);
        Task task2 = new Task("something", "doing things");
        task2.setId(2);
        Task task3 = new Task("lose weight", "do some sport");
        task3.setId(3);
        historyManager.addHistory(task1);
        historyManager.addHistory(task2);
        historyManager.addHistory(task3);
        assertEquals(List.of(task1, task2, task3), historyManager.getHistory());
    }



}