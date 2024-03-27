package task;


import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {

    private final Integer epicId;
    public SubTask(Integer epicId, String name, String description, LocalDateTime startTime, long duration) {
        super(name, description, startTime, duration);
        this.epicId = Objects.requireNonNull(epicId, "У подзадачи должен быть указан эпик.");
    }

    public SubTask(Integer epicId, String name, String description) {
        super(name, description);
        this.epicId = Objects.requireNonNull(epicId, "У подзадачи должен быть указан эпик.");
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public void setId(Integer id) {
        if (!Objects.equals(this.epicId, id)) {
            this.id = id;
        }
        else {
            throw new IllegalArgumentException("Подзадача не может быть своим же эпиком");
        }
    }

}
