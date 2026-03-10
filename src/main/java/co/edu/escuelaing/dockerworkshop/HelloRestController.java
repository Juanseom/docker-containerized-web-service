package co.edu.escuelaing.dockerworkshop;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloRestController {

    private static final Logger LOGGER = Logger.getLogger(HelloRestController.class.getName());
    private static final String TEMPLATE = "Hello, %s!";


    public static String greeting(String query) throws Exception {
        Map<String, String> params = parseQuery(query);
        String name = params.getOrDefault("name", "World");
        String response = String.format(TEMPLATE, name);
        LOGGER.info("Greeting request for name: " + name);
        return response;
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int eqIdx = pair.indexOf('=');
            if (eqIdx > 0) {
                try {
                    String key = URLDecoder.decode(pair.substring(0, eqIdx), StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(pair.substring(eqIdx + 1), StandardCharsets.UTF_8);
                    params.put(key, value);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error parsing query parameter: " + pair, e);
                }
            }
        }
        return params;
    }
}

