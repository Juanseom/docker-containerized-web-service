package co.edu.escuelaing.dockerworkshop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class SimpleHttpServerIntegrationTest {

    private SimpleHttpServer server;

    @AfterEach
    void tearDown() {
        if (server != null && server.isRunning()) {
            server.shutdown();
        }
    }

    @Test
    void shouldServeGreetingEndpoint() throws Exception {
        startServer();

        HttpResponse<String> response = sendRequest("GET", "/greeting?name=Luisa");

        assertEquals(200, response.statusCode());
        assertEquals("Hello, Luisa!", response.body());
    }

    @Test
    void shouldRejectNonGetMethods() throws Exception {
        startServer();

        HttpResponse<String> response = sendRequest("POST", "/greeting?name=Luisa");

        assertEquals(405, response.statusCode());
        assertTrue(response.body().contains("Method Not Allowed"));
    }

    private void startServer() throws IOException {
        server = new SimpleHttpServer(0);
        server.registerGET("/greeting", HelloRestController::greeting);
        server.start();
    }

    private HttpResponse<String> sendRequest(String method, String path) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + server.getPort() + path))
                .method(method, HttpRequest.BodyPublishers.noBody())
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}

