package net.ttddyy.testcontexts;

import net.ttddyy.testcontexts.core.ConfiguredContext;
import net.ttddyy.testcontexts.core.ConfiguredContextDefinition;
import net.ttddyy.testcontexts.core.TestConfig;
import net.ttddyy.testcontexts.core.listener.RefreshContext;
import net.ttddyy.testcontexts.core.suport.testng.AbstractTestNGSupport;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @author Tadaya Tsuyukubo
 */
public class RefreshContextTestEventListenerITest {

    @Test
    public void testWithTestNG() {
        TestNGUtils.runAndVerify(RefreshContextWithClassAnnotationTestCase.class);
        TestNGUtils.runAndVerify(RefreshContextWithMethodAnnotationTestCase.class);
    }


    @TestConfig(context = "foo")
    @RefreshContext(contexts = "foo", classMode = RefreshContext.ClassMode.AFTER_EACH_TEST_METHOD)
    public static class RefreshContextWithClassAnnotationTestCase extends AbstractTestNGSupport {

        static {
            configClasses.clear();
            configClasses.add(TestConfiguration.class);
        }


        @Resource
        private StringHolder holder;

        @org.testng.annotations.Test
        public void testA() {
            assertThat(holder, notNullValue());
            assertThat(holder.getValue(), is("foo"));

            holder.setValue("bar");
            assertThat(holder.getValue(), is("bar"));

        }

        @org.testng.annotations.Test
        public void testB() {
            assertThat(holder, notNullValue());
            assertThat(holder.getValue(), is("foo"));

            holder.setValue("bar");
            assertThat(holder.getValue(), is("bar"));
        }

    }

    @TestConfig(context = "foo")
    public static class RefreshContextWithMethodAnnotationTestCase extends AbstractTestNGSupport {

        static {
            configClasses.clear();
            configClasses.add(TestConfiguration.class);
        }


        @Resource
        private StringHolder holder;

        @org.testng.annotations.Test
        @RefreshContext(contexts = "foo")
        public void testA() {
            assertThat(holder, notNullValue());
            assertThat(holder.getValue(), is("foo"));

            holder.setValue("bar");
            assertThat(holder.getValue(), is("bar"));

        }

        // depends on testA, so that always testB comes after testA
        @org.testng.annotations.Test(dependsOnMethods = "testA")
        public void testB() {
            assertThat(holder, notNullValue());
            assertThat(holder.getValue(), is("foo"));
        }

    }


    @ConfiguredContextDefinition
    public static class TestConfiguration {

        @ConfiguredContext(name = "foo", classes = AppContext.class)
        public void context() {
        }
    }

    @Configuration
    public static class AppContext {
        @Bean
        public StringHolder foo() {
            return new StringHolder("foo");
        }
    }


}
