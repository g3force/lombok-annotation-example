package test;

import de.qaware.lombok.Hello;

public class AnnotatedMethod {
    @Hello
    void hello() {
        System.out.println("Hello World");
    }
}