package co.edu.escuelaing.dockerworkshop;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SimpleHttpServer {

    private static final Logger LOGGER = Logger.getLogger(SimpleHttpServer.class.getName());
    private final HttpServer server;
    private final ExecutorService executor;
    private volatile boolean running = true;

    public SimpleHttpServer(int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.executor = Executors.newFixedThreadPool(Math.max(4, Runtime.getRuntime().availableProcessors() * 2));
        this.server.setExecutor(executor);
        LOGGER.info("HTTP Server initialized on port " + port);
    }


    public void registerGET(String path, RequestHandler handler) {
        HttpContext context = server.createContext(path);
        context.setHandler(exchange -> {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }
            try {
                String response = handler.handle(exchange.getRequestURI().getQuery());
                sendResponse(exchange, 200, response);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error handling request", e);
                sendResponse(exchange, 500, "Internal Server Error");
            }
        });
        LOGGER.info("Registered GET endpoint: " + path);
    }

    public void start() {
        server.start();
        LOGGER.info("HTTP Server started");
    }


    public void shutdown() {
        if (!running) {
            return;
        }
        running = false;
        LOGGER.info("Shutting down HTTP Server...");

        server.stop(5);
        LOGGER.info("HTTP Server stopped");

        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    LOGGER.warning("Executor did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        LOGGER.info("Executor shutdown complete");
    }

    private void sendResponse(com.sun.net.httpserver.HttpExchange exchange, int statusCode, String responseBody) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBody.getBytes(java.nio.charset.StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBody.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    @FunctionalInterface
    public interface RequestHandler {
        String handle(String query) throws Exception;
    }

    public boolean isRunning() {
        return running;
    }
}

