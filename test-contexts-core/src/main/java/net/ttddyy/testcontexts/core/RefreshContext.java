package net.ttddyy.testcontexts.core;

import java.lang.annotation.*;

/**
 * @author Tadaya Tsuyukubo
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RefreshContext {

    static enum ClassMode {
        AFTER_CLASS,
        AFTER_EACH_TEST_METHOD
    }

    ClassMode classMode() default ClassMode.AFTER_CLASS;

    String[] contexts() default {};

}
