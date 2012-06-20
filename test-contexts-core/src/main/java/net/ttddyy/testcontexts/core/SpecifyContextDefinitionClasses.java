package net.ttddyy.testcontexts.core;

import java.lang.annotation.*;

/**
 * @author Tadaya Tsuyukubo
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SpecifyContextDefinitionClasses {

    Class<?>[] classes() default {};
}
