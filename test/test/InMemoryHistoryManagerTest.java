package test;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;

class InMemoryHistoryManagerTest extends HistoryManagerTest<HistoryManager>{




    @BeforeEach
    public void createAllFields() {
        historyManager = new InMemoryHistoryManager();
        super.createAllFields();
    }





}