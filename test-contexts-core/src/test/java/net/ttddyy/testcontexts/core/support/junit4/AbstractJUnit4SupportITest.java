package net.ttddyy.testcontexts.core.support.junit4;

import net.ttddyy.testcontexts.core.*;
import net.ttddyy.testcontexts.core.suport.junit4.AbstractJUnit4Support;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
public class AbstractJUnit4SupportITest {

    private static boolean isFooFinished = false;
    private static boolean isBarFinished = false;

    @Test
    public void test() {
        TestUtils.clearContextManager();

        JUnit4Utils.runAndVerify(AbstractJUnit4SupportTestCaseFoo.class, AbstractJUnit4SupportTestCaseBar.class);
        assertThat(isFooFinished, is(true));
        assertThat(isBarFinished, is(true));
    }


    @TestConfig(context = "foo")
    public static class AbstractJUnit4SupportTestCaseFoo extends MyJUnit4Support {

        @Resource
        private String foo;

        @Test
        public void test() {
            assertThat(foo, is("foo"));
            isFooFinished = true;
        }

    }

    @TestConfig(context = "foo")
    public static class AbstractJUnit4SupportTestCaseBar extends MyJUnit4Support {
        @Autowired
        private String bar;

        @Test
        public void test() {
            assertThat(bar, is("bar"));
            isBarFinished = true;
        }


    }

    @SpecifyContextDefinitionClasses(classes = ConfigClass.class)
    public static class MyJUnit4Support extends AbstractJUnit4Support {

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

        @Bean
        public String bar() {
            return "bar";
        }
    }


}
