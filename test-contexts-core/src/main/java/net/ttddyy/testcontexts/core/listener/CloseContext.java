package net.ttddyy.testcontexts.core.listener;

import java.lang.annotation.*;

/**
 * @author Tadaya Tsuyukubo
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CloseContext {

    String[] contexts() default {};

}
