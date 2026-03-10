package co.edu.escuelaing.dockerworkshop;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloRestController {

    private static final String TEMPLATE = "Hello, %s!";

    @GetMapping({"/greeting", "/hello"})
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format(TEMPLATE, name);
    }
}

