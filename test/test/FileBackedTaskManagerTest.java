package test;

import manager.FileBackedTaskManager;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


import static org.junit.jupiter.api.Assertions.*;


class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {


    private  static File tempFile;

    @BeforeEach
    public void createAllFields() throws IOException {

        tempFile = Files.createTempFile("test", ".tmp").toFile();
        taskManager = new FileBackedTaskManager(tempFile);
        super.beforeEachTest();

    }
    @Test
    void loadFileTest() throws IOException {
        var manager = FileBackedTaskManager.loadFromFile(tempFile);
        Files.readAllLines(tempFile.toPath());
        assertNotNull(manager);
        assertTrue(Files.readAllLines(tempFile.toPath()).isEmpty());
    }


}
