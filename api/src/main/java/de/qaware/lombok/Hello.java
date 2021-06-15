package de.qaware.lombok;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * A custom Lombok annotation to say hello.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Hello {
    /**
     * The recipient to greet.
     *
     * @return the recipient.
     */
    String recipient() default "World";
}
