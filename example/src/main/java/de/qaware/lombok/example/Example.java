package de.qaware.lombok.example;

import de.qaware.lombok.Hello;
import lombok.extern.slf4j.Slf4j;

/**
 * Example application that demonstrates the annotation.
 */
@Slf4j
public class Example {
    /**
     * Entry point.
     *
     * @param args application arguments.
     */
    public static void main(String[] args) {
        log.info("Run the example");
        var example = new Example();
        example.helloWorld();
        log.info("Done");
    }

    @Hello(recipient = "QAware")
    private void helloWorld() {
        // empty
    }
}
