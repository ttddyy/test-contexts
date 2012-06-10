package net.ttddyy.testcontexts.core;

import org.springframework.context.ApplicationListener;

import java.lang.annotation.*;

/**
 * Specify which configured context to use in test class.
 *
 * TODO: allow method level annotation?
 *
 * @author Tadaya Tsuyukubo
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TestConfig {

    String context() default "";

    Class<? extends ApplicationListener<? extends TestLifecycleEvent>>[] listeners() default {};

}
