package net.ttddyy.testcontexts.core.listener;

import java.lang.annotation.*;

/**
 * After execution of annotated class/method, specified context will be re-created.
 * <p/>
 * similar to @DirtiesContext in spring, but also you can specify which contexts to be refreshed.
 *
 * @author Tadaya Tsuyukubo
 * @see net.ttddyy.testcontexts.core.listener.RefreshContextTestEventListener
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
