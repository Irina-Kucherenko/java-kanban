package test;

import org.junit.jupiter.api.Test;
import task.Task;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void taskEqualsTest() {
        Task task1 = new Task("Домашнее задание от мамы", "Убрать хату, помыть посуду");
        Task task2= new Task("Новый год: докупить", "Джин, вино, ягермейстер");
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2);
    }

}