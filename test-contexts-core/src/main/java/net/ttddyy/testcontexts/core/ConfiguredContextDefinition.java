package net.ttddyy.testcontexts.core;

import java.lang.annotation.*;

/**
 * Annotate to the classes that define Configured Contexts.
 *
 * @author Tadaya Tsuyukubo
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ConfiguredContextDefinition {
}
