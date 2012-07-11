package net.ttddyy.testcontexts.core.listener;

import java.lang.annotation.*;

/**
 * Annotation for closing configured-contexts on after class.
 *
 * @author Tadaya Tsuyukubo
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CloseContext {

    /**
     * Configured-context names
     *
     * @return configured-context names
     */
    String[] contexts() default {};

}
