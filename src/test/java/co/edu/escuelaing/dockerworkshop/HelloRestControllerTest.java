package co.edu.escuelaing.dockerworkshop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HelloRestControllerTest {

    @Test
    void shouldReturnDefaultGreetingWhenNameIsMissing() throws Exception {
        String response = HelloRestController.greeting(null);
        assertEquals("Hello, World!", response);
    }

    @Test
    void shouldReadNameFromQueryString() throws Exception {
        String response = HelloRestController.greeting("name=Carlos");
        assertEquals("Hello, Carlos!", response);
    }

    @Test
    void shouldDecodeUrlEncodedName() throws Exception {
        String response = HelloRestController.greeting("name=Ana%20Maria");
        assertEquals("Hello, Ana Maria!", response);
    }
}

