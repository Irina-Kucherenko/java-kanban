package manager;

import java.io.File;

public class Managers {

    public TaskManager getDefault(){
        return FileBackedTaskManager.loadFromFile(new File("C:\\java-kanban-file\\TaskFile.txt"));
    }



    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
