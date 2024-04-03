package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.*;
import server.HttpTaskServer;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;
import utils.DurationAdapter;
import utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class HttpTaskServerTest {
    private HttpTaskServer server;
    HttpClient client = HttpClient.newHttpClient();
    private final String BASE_PATH = "http://localhost:9000/";
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private void createData() {
        Task task1 = new Task("Домашнее задание от мамы", "Убрать хату -> помыть посуду", LocalDateTime.now(), 5);
        task1.setId(1);
        tasks.put(1, task1);
        Epic epic1 = new Epic("Проект", "Разработать проект");
        epic1.setId(2);
        epics.put(2, epic1);
        SubTask subTask1 = new SubTask(epic1.getId(), "Закончить прокрастинировать", "1. Встать с дивана -> " +
                "2. Сесть за ПК", LocalDateTime.now().plusHours(2), 10);
        subTask1.setId(3);
        subTasks.put(3, subTask1);
        SubTask subTask2 = new SubTask(epic1.getId(), "Начать делать проект", "1. Открыть среду -> " +
                " 2.Создать файлик -> 3. На этом всё поработали", LocalDateTime.now().plusHours(3), 10);
        subTask2.setId(4);
        subTasks.put(4, subTask2);
        epic1.addSubTask(subTask1);
        epic1.addSubTask(subTask2);


    }
    @BeforeEach
    public void createServer() {
        try {
            createData();
            TaskManager taskManager = new InMemoryTaskManager(tasks, epics, subTasks, 5);
            server = new HttpTaskServer(taskManager);
            server.startServer();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @AfterEach
    public void stop() {
        server.stopServer();
    }

    @Test
    void getTasksTest() {
        URI path = URI.create(BASE_PATH + "task");
        HttpRequest request = HttpRequest.newBuilder().uri(path).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());

            List<Task> task = gson.fromJson(response.body(), List.class);
            Assertions.assertEquals(1, task.size());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getTaskByIdTest() {
        URI path = URI.create(BASE_PATH + "task/1");
        HttpRequest request = HttpRequest.newBuilder().uri(path).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());

            Task task = gson.fromJson(response.body(), Task.class);
            Assertions.assertEquals(1, task.getId());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void postTaskCreateTest() {
        URI path = URI.create(BASE_PATH + "task");
        Task task = new Task("project", "write smth", LocalDateTime.now().plusHours(1), 5);
        HttpRequest request = HttpRequest.newBuilder().uri(path).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(201, response.statusCode());
            Task taskResponse = gson.fromJson(response.body(), Task.class);
            Assertions.assertEquals(6, taskResponse.getId());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void postTaskUpdateTest() {
        URI path = URI.create(BASE_PATH + "task");
        Task task = new Task("rrr", "ttt", LocalDateTime.now(), 5);
        task.setId(1);
        task.setStatus(TaskStatus.IN_PROGRESS);
        HttpRequest request = HttpRequest.newBuilder().uri(path).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(201, response.statusCode());
            Assertions.assertEquals("Задача успешно обновлена", response.body().replace("\"", ""));

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteTaskByIdTest() {
        URI path = URI.create(BASE_PATH + "task/1");
        HttpRequest request = HttpRequest.newBuilder().uri(path).DELETE().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());

            Assertions.assertEquals("Задача успешно удалена", response.body().replace("\"", ""));

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteTasksTest() {
        URI path = URI.create(BASE_PATH + "task");
        HttpRequest request = HttpRequest.newBuilder().uri(path).DELETE().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());

            Assertions.assertEquals("Задачи успешно удалены", response.body().replace("\"", ""));

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getSubtasksTest() {
        URI path = URI.create(BASE_PATH + "subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(path).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());

            List<SubTask> subtask = gson.fromJson(response.body(), List.class);
            Assertions.assertEquals(2, subtask.size());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getSubtaskByIdTest() {
        URI path = URI.create(BASE_PATH + "subtask/4");
        HttpRequest request = HttpRequest.newBuilder().uri(path).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());

            SubTask subtask = gson.fromJson(response.body(), SubTask.class);
            Assertions.assertEquals(4, subtask.getId());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void postSubtaskCreateTest() {
        URI path = URI.create(BASE_PATH + "subtask");
        SubTask subTask = new SubTask(2, "project", "write smth", LocalDateTime.now().plusHours(1), 5);
        HttpRequest request = HttpRequest.newBuilder().uri(path).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask))).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(201, response.statusCode());
            SubTask subtaskResponse = gson.fromJson(response.body(), SubTask.class);
            Assertions.assertEquals(6, subtaskResponse.getId());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void postSubtaskUpdateTest() {
        URI path = URI.create(BASE_PATH + "subtask");
        SubTask subTask = new SubTask(2,"rrr", "ttt", LocalDateTime.now(), 5);
        subTask.setId(3);
        subTask.setStatus(TaskStatus.IN_PROGRESS);
        HttpRequest request = HttpRequest.newBuilder().uri(path).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask))).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(201, response.statusCode());
            Assertions.assertEquals("Подзадача успешно обновлена", response.body().replace("\"", ""));

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteSubtaskByIdTest() {
        URI path = URI.create(BASE_PATH + "subtask/3");
        HttpRequest request = HttpRequest.newBuilder().uri(path).DELETE().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());

            Assertions.assertEquals("Подзадача успешно удалена", response.body().replace("\"", ""));

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteSubtasksTest() {
        URI path = URI.create(BASE_PATH + "subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(path).DELETE().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());

            Assertions.assertEquals("Подзадачи успешно удалены", response.body().replace("\"", ""));

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getEpicsTest() {
        URI path = URI.create(BASE_PATH + "epic");
        HttpRequest request = HttpRequest.newBuilder().uri(path).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());

            List<Epic> epics = gson.fromJson(response.body(), List.class);
            Assertions.assertEquals(1, epics.size());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getEpicByIdTest() {
        URI path = URI.create(BASE_PATH + "epic/2");
        HttpRequest request = HttpRequest.newBuilder().uri(path).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());

            Epic epic = gson.fromJson(response.body(), Epic.class);
            Assertions.assertEquals(2, epic.getId());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getEpicSubtaskListTest() {
        URI path = URI.create(BASE_PATH + "epic/2/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(path).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());


            List<SubTask> subTasks = gson.fromJson(response.body(), List.class);

            Assertions.assertEquals(2, subTasks.size());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void postEpicCreateTest() {
        URI path = URI.create(BASE_PATH + "epic");
        Epic epic = new Epic( "project", "write smth");
        HttpRequest request = HttpRequest.newBuilder().uri(path).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(201, response.statusCode());
            Epic epicResponse = gson.fromJson(response.body(), Epic.class);
            Assertions.assertEquals(6, epicResponse.getId());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteEpicByIdTest() {
        URI path = URI.create(BASE_PATH + "epic/2");
        HttpRequest request = HttpRequest.newBuilder().uri(path).DELETE().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());

            Assertions.assertEquals("Эпик успешно удален", response.body().replace("\"", ""));

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteEpicsTest() {
        URI path = URI.create(BASE_PATH + "epic");
        HttpRequest request = HttpRequest.newBuilder().uri(path).DELETE().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());

            Assertions.assertEquals("Эпики успешно удалены", response.body().replace("\"", ""));

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



}
