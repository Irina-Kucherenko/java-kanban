package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.*;
import task.Epic;
import task.SubTask;
import task.Task;
import utils.DurationAdapter;
import utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskServer {

    private static final int PORT = 9000;
    private static final String ERROR_MESSAGE = "Передан неподдерживаемый запрос.";

    private final HttpServer httpServer;

    private final TaskManager taskManager;

    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HttpTaskServer() throws IOException {
        Managers manager = new Managers();
        taskManager = manager.getDefault();
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/task", new TasksHandler());
        httpServer.createContext("/epic", new EpicsHandler());
        httpServer.createContext("/subtask", new SubTasksHandler());
        httpServer.createContext("/history", new HistoryHandler());
        httpServer.createContext("/prioritized", new PrioritizedHandler());
        httpServer.setExecutor(null);
    }

    public void startServer() {
        httpServer.start();
        System.out.println("Порт " + PORT + ": HTTP-сервер запущен!");

    }

    public void stopServer() {
        httpServer.stop(0);
        System.out.println("Порт " + PORT + ": HTTP-сервер остановлен!");
    }

    private void sendResponse(HttpExchange exchange, Object body, int code) {
        try {
            String response = gson.toJson(body);
            exchange.sendResponseHeaders(code, 0);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Произошла ошибка при отправке ответа: " + e.getMessage());
        }
    }



    class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            URI path = exchange.getRequestURI();
            String[] stringPath = path.toString().split("/");
            String method = exchange.getRequestMethod();
            if (method.equals("GET")) {
                if (stringPath.length == 2) {
                    List<Task> history = taskManager.getHistoryManager().getHistory();
                    sendResponse(exchange, history, 200);
                }
            } else {
                throw new IllegalArgumentException(ERROR_MESSAGE);
            }
            exchange.close();
        }
    }


    class TasksHandler implements HttpHandler {


        private void taskGetRequest(String[] stringPath, HttpExchange exchange) {

                if (stringPath.length == 2) {
                    List<Task> tasks = taskManager.getTasks();
                    sendResponse(exchange, tasks, 200);
                } else if (stringPath.length == 3) {
                    Integer id = Integer.parseInt(stringPath[2]);
                    Task task = taskManager.getTask(id);
                    if (task == null) {
                        sendResponse(exchange, "Задача не найдена", 404);
                    } else {
                        sendResponse(exchange, task, 200);
                    }
                }

        }

        private void taskPostRequest(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();
            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(json, Task.class);
            try {
                if (task.getId() == null) {
                    task = taskManager.createTask(task);
                    sendResponse(exchange, task, 201);
                } else {
                    taskManager.updateTask(task);
                    sendResponse(exchange, "Задача успешно обновлена", 201);
                }
            } catch (ManagerCreateException e) {
                sendResponse(exchange, "Временное пересечение", 406);
            }
        }

        private void taskDeleteRequest(String[] stringPath, HttpExchange exchange) {
            if (stringPath.length == 2) {
                taskManager.deleteTasks();
                sendResponse(exchange, "Задачи успешно удалены", 200);
            } else if (stringPath.length == 3) {
                Integer id = Integer.parseInt(stringPath[2]);
                try {
                    taskManager.deleteTask(id);
                    sendResponse(exchange, "Задача успешно удалена", 200);
                } catch (IllegalArgumentException e) {
                    sendResponse(exchange, e.getMessage(), 404);
                }

            }
        }
        @Override
        public void handle(HttpExchange exchange) {
            try {
                URI path = exchange.getRequestURI();
                String[] stringPath = path.toString().split("/");
                String method = exchange.getRequestMethod();
                switch (method) {
                    case "GET" -> taskGetRequest(stringPath, exchange);
                    case "POST" -> taskPostRequest(exchange);
                    case "DELETE" -> taskDeleteRequest(stringPath, exchange);
                    default -> throw new IllegalArgumentException(ERROR_MESSAGE);
                }
            } catch (Exception e) {
                sendResponse(exchange, "Произошла ошибка при обработке запроса: " + e.getMessage(), 500);
            }
            exchange.close();
        }


    }

    class SubTasksHandler implements HttpHandler {

        private void subtaskGetRequest(String[] stringPath, HttpExchange exchange)  {
            if (stringPath.length == 2) {
                List<SubTask> subTasks = taskManager.getSubTasks();
                sendResponse(exchange, subTasks, 200);
            } else if (stringPath.length == 3) {
                Integer id = Integer.parseInt(stringPath[2]);
                SubTask subTask = taskManager.getSubTask(id);
                if (subTask == null) {
                    sendResponse(exchange, "Подзадача не найдена", 404);
                } else {
                    sendResponse(exchange, subTask, 200);
                }
            }
        }

        private void subtaskPostRequest(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();
            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            SubTask subTask = gson.fromJson(json, SubTask.class);
            try {
                if (subTask.getId() == null) {
                    subTask = taskManager.createSubTask(subTask);
                    sendResponse(exchange, subTask, 201);
                } else {
                    taskManager.updateSubTask(subTask);
                    sendResponse(exchange, "Подзадача успешно обновлена", 201);
                }
            } catch (ManagerCreateException e) {
                sendResponse(exchange, "Временное пересечение", 406);
            }
        }

        private void subtaskDeleteRequest(String[] stringPath, HttpExchange exchange) throws IOException {
            if (stringPath.length == 1) {
                taskManager.deleteSubTasks();
                exchange.sendResponseHeaders(200, 0);
            } else if (stringPath.length == 2) {
                Integer id = Integer.parseInt(stringPath[1]);
                taskManager.deleteSubTask(id);
                exchange.sendResponseHeaders(200, 0);
            }
        }
        @Override
        public void handle(HttpExchange exchange) {
            try {
                URI path = exchange.getRequestURI();
                String[] stringPath = path.toString().split("/");
                String method = exchange.getRequestMethod();
                switch (method) {
                    case "GET" -> subtaskGetRequest(stringPath, exchange);
                    case "POST" -> subtaskPostRequest(exchange);
                    case "DELETE" -> subtaskDeleteRequest(stringPath, exchange);
                    default -> throw new IllegalArgumentException(ERROR_MESSAGE);
                }
            } catch (Exception e) {
                sendResponse(exchange, "Произошла ошибка при обработке запроса: " + e.getMessage(), 500);
            }
            exchange.close();

        }
    }

    class EpicsHandler implements HttpHandler {

        private void epicGetRequest(String[] stringPath, HttpExchange exchange) throws IOException {
            if (stringPath.length == 1) {
                List<Epic> epics = taskManager.getEpics();
                sendResponse(exchange, epics, 200);
            } else if (stringPath.length == 2) {
                Integer id = Integer.parseInt(stringPath[1]);
                Epic epic = taskManager.getEpic(id);
                if (epic == null) {
                    exchange.sendResponseHeaders(404, 0);
                } else {
                    sendResponse(exchange, epic, 200);
                }
            } else if (stringPath.length == 3) {
                Integer id = Integer.parseInt(stringPath[1]);
                Epic epic = taskManager.getEpic(id);
                if (epic == null) {
                    exchange.sendResponseHeaders(404, 0);
                } else {
                    List<SubTask> subTaskOfEpic = taskManager.getSubTasksOfEpic(epic.getId());
                    sendResponse(exchange, subTaskOfEpic, 200);
                }
            }
        }

        private void epicPostRequest(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();
            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(json, Epic.class);
            try {
                if (epic.getId() == null) {
                    epic = taskManager.createEpic(epic);
                    sendResponse(exchange, epic, 201);
                }
            } catch (ManagerCreateException e) {
                exchange.sendResponseHeaders(406, 0);
            }
        }

        private void epicDeleteRequest(String[] stringPath, HttpExchange exchange) throws IOException {
            if (stringPath.length == 1) {
                taskManager.deleteEpics();
                exchange.sendResponseHeaders(200, 0);
            } else if (stringPath.length == 2) {
                Integer id = Integer.parseInt(stringPath[1]);
                taskManager.deleteEpic(id);
                exchange.sendResponseHeaders(200, 0);
            }
        }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI path = exchange.getRequestURI();
            String[] stringPath = path.toString().split("/");
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET" -> epicGetRequest(stringPath, exchange);
                case "POST" -> epicPostRequest(exchange);
                case "DELETE" -> epicDeleteRequest(stringPath, exchange);
                default -> throw new IllegalArgumentException(ERROR_MESSAGE);
            }
        }
    }

    class PrioritizedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            URI path = exchange.getRequestURI();
            String[] stringPath = path.toString().split("/");
            String method = exchange.getRequestMethod();
            if (method.equals("GET")) {
                if (stringPath.length == 1) {
                    List<Task> prioritized = taskManager.getPrioritizedTasks();
                    sendResponse(exchange, prioritized, 200);
                }
            } else {
                throw new IllegalArgumentException(ERROR_MESSAGE);
            }
            exchange.close();
        }
    }
}

//исправить обработку эпика и приоритайзд, сделать тесты #TODO









