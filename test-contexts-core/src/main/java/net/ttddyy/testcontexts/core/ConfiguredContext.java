package net.ttddyy.testcontexts.core;

import org.springframework.context.ApplicationListener;

import java.lang.annotation.*;

/**
 * Describe the configured-context attributes.
 *
 * @author Tadaya Tsuyukubo
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ConfiguredContext {

    /**
     * Context name
     *
     * @return context name
     */
    String name() default "";

    /**
     * Parent context name
     *
     * @return parent context name
     */
    String parent() default "";

    /**
     * Active spring profile names
     *
     * @return spring profile names
     */
    String[] profiles() default {};

    /**
     * Spring ApplicationContext xml config files
     *
     * @return xml config files
     */
    String[] locations() default {};

    /**
     * Spring ApplicationContext java config classes
     *
     * @return java config files
     */
    Class<?>[] classes() default {};

    /**
     * Test listeners
     *
     * @return test listeners
     */
    Class<? extends ApplicationListener<? extends TestLifecycleEvent>>[] listeners() default {};

}
