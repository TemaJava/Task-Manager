package http;

import com.sun.net.httpserver.HttpServer;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

public class HttpTaskServer {
    HttpServer server;
    private static final int PORT = 8040;

    public HttpTaskServer(TaskManager taskService) throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler(taskService));
        server.start();
        System.out.println("HttpServer запущен на порт: " + PORT);
    }

    public void stop() {
        server.stop(0);
    }
}
