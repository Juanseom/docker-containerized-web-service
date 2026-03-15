package co.edu.escuelaing.dockerworkshop;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RestServiceApplication {

    private static final Logger LOGGER = Logger.getLogger(RestServiceApplication.class.getName());

    public static void main(String[] args) {
        int port = getPort();
        LOGGER.info("Starting RestServiceApplication on port " + port);

        try {
            SimpleHttpServer server = new SimpleHttpServer(port);

            server.registerGET("/greeting", HelloRestController::greeting);
            server.registerGET("/hello", HelloRestController::greeting);

            server.start();
            LOGGER.info("Server is running. Press Ctrl+C to stop.");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOGGER.info("Shutdown signal received");
                server.shutdown();
            }));

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to start server", e);
            System.exit(1);
        }
    }

    private static int getPort() {
        String portEnv = System.getenv("PORT");
        if (portEnv != null) {
            try {
                return Integer.parseInt(portEnv);
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid PORT environment variable, using default", e);
            }
        }
        return 5000;
    }
}

