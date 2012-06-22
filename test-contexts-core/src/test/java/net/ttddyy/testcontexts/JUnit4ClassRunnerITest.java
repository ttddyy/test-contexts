package net.ttddyy.testcontexts;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.ttddyy.testcontexts.core.ConfiguredContext;
import net.ttddyy.testcontexts.core.ConfiguredContextDefinition;
import net.ttddyy.testcontexts.core.TestConfig;
import net.ttddyy.testcontexts.core.suport.junit4.TestContextsJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.model.InitializationError;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Tadaya Tsuyukubo
 */
public class JUnit4ClassRunnerITest {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Test
    public void test() {
        JUnitCore jUnitCore = new JUnitCore();

        Result result = jUnitCore.run(JUnit4ClassRunnerTestCaseFoo.class);

        if (!result.wasSuccessful()) {

            List<Failure> failures = result.getFailures();

            String header = String.format("Combined Messages (Total:%d)", failures.size());

            List<String> errorMessages = Lists.newArrayList();
            errorMessages.add(header);
            errorMessages.addAll(Lists.transform(failures, new Function<Failure, String>() {
                int i = 1;

                @Override
                public String apply(Failure failure) {
                    String stackTraceString = Throwables.getStackTraceAsString(failure.getException());
                    String template = "Message-%d: %n %s";
                    return String.format(template, i++, stackTraceString);
                }
            }));

            // transform messages to a single combined string
            String message = Joiner.on(LINE_SEPARATOR).join(errorMessages);
            throw new AssertionError(message);
        }

    }

//    static {
//        TestManager.CONFIG_CLASSES.clear();
//        TestManager.CONFIG_CLASSES.add(ConfigClass.class);
//    }


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

    @RunWith(MyParentRunner.class)
    @TestConfig(context = "foo")
    public static class JUnit4ClassRunnerTestCaseFoo {
//        static {
//            TestManager.CONFIG_CLASSES.clear();
//            TestManager.CONFIG_CLASSES.add(ConfigClass.class);
//        }

//        public JUnit4ClassRunnerTestCaseFoo() {
//            TestManager.CONFIG_CLASSES.clear();
//            TestManager.CONFIG_CLASSES.add(ConfigClass.class);
//        }

        @Test
        public void testFoo() {
            assertThat(true, is(true));
//            assertThat(true, is(false));
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
