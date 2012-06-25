package net.ttddyy.testcontexts.core.support.junit4;

import com.google.common.collect.Sets;
import net.ttddyy.testcontexts.core.ConfiguredContext;
import net.ttddyy.testcontexts.core.ConfiguredContextDefinition;
import net.ttddyy.testcontexts.core.TestConfig;
import net.ttddyy.testcontexts.core.suport.junit4.TestContextsJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Test for @TestContextsJUnit4ClassRunner.
 *
 * @author Tadaya Tsuyukubo
 */
public class JUnit4ClassRunnerITest {

    @Test
    public void test() {

        JUnit4Utils.runAndVerify(JUnit4ClassRunnerTestCaseFoo.class);

    }

    @RunWith(MyParentRunner.class)
    @TestConfig(context = "foo")
    public static class JUnit4ClassRunnerTestCaseFoo {

        @Resource
        private String foo;

        @Test
        public void testFoo() {
            assertThat(foo, is("foo"));
        }

    }


    public static class MyParentRunner extends TestContextsJUnit4ClassRunner {
        public MyParentRunner(Class<?> klass) throws InitializationError {
            super(klass);
        }

        @Override
        public Set<Class<?>> getConfigurationDefinitionClasses() {
            Set<Class<?>> set = Sets.newHashSet();
            set.add(ConfigClass.class);
            return set;
        }
    }


    @ConfiguredContextDefinition
    public static class ConfigClass {

        @ConfiguredContext(name = "foo", classes = SpringContext.class)
        public void ctxFoo() {
        }
    }

    @Configuration
    public static class SpringContext {

        @Bean
        public String foo() {
            return "foo";
        }
    }
}
