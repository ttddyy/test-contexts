package net.ttddyy.testcontexts.core;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Tadaya Tsuyukubo
 */
public class ConfiguredContextAnnotationUtilsTests {

    @Test
    public void testGetAnnotatedMethods() {

        class ConfigFoo {
            @ConfiguredContext
            void annotatedA() {
            }

            @ConfiguredContext
            void annotatedB() {
            }

            void notAnnotated() {
            }
        }
        class ConfigBar {
            void notAnnotated() {
            }

            @ConfiguredContext
            void annotatedA() {
            }
        }


        List<Method> methods = ConfiguredContextAnnotationUtils.getAnnotatedMethods(ConfigFoo.class, ConfigBar.class);

        assertThat(methods, is(hasSize(3)));
        List<String> methodNames = Lists.transform(methods, new Function<Method, String>() {
            @Override
            public String apply(Method method) {
                return method.getName();
            }
        });

        assertThat(methodNames, hasItems("annotatedA", "annotatedB"));

    }
}
