package test;

import manager.HistoryManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import task.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class HistoryManagerTest<T extends HistoryManager> {

    protected static HistoryManager historyManager;
    protected static Task task1;
    protected final static String name = "Project";
    protected final static String description = "Do!";

     void createAllFields() {
        task1 = new Task(name, description, LocalDateTime.now(), 5);

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

    @ParameterizedTest
    @CsvSource({"1", "2", "3"})
    void removeTest(Integer id) {
        task1.setId(1);
        Task task2 = new Task("something", "doing things", LocalDateTime.now().plusHours(1), 5);
        task2.setId(2);
        Task task3 = new Task("lose weight", "do some sport", LocalDateTime.now().plusHours(2), 5);
        task3.setId(3);

        historyManager.addHistory(task1);
        historyManager.addHistory(task2);
        historyManager.addHistory(task3);


        historyManager.remove(id);
        assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    void addHistory() {
        task1.setId(1);
        Task task2 = new Task("something", "doing things", LocalDateTime.now().plusHours(1), 5);
        task2.setId(2);
        Task task3 = new Task("lose weight", "do some sport", LocalDateTime.now().plusHours(2), 5);
        task3.setId(3);
        historyManager.addHistory(task1);
        historyManager.addHistory(task2);
        historyManager.addHistory(task3);
        assertEquals(List.of(task1, task2, task3), historyManager.getHistory());
    }

    @Test
    void checkDuplicateHistory() {
        Task task2 = new Task("something", "doing things", LocalDateTime.now().plusHours(1), 5);
        task2.setId(2);
        historyManager.addHistory(task2);
        historyManager.addHistory(task2);
        assertEquals(1, historyManager.getHistory().size());
    }
}
