package net.ttddyy.testcontexts;

import net.ttddyy.testcontexts.core.ConfiguredContext;
import net.ttddyy.testcontexts.core.ConfiguredContextDefinition;
import net.ttddyy.testcontexts.core.TestConfig;
import net.ttddyy.testcontexts.core.listener.CloseContext;
import net.ttddyy.testcontexts.core.suport.testng.AbstractTestNGSupport;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Tadaya Tsuyukubo
 */
public class CloseContextTestEventListenerITest {

    @Test
    public void testByTestNG() {
        TestNGUtils.runAndVerify(CloseContextTestTestCase.class);
    }

    @TestConfig
    @CloseContext(contexts = {"foo", "bar"})
    public static class CloseContextTestTestCase extends AbstractTestNGSupport {

        static {
            configClasses.clear();
            configClasses.add(TestConfiguration.class);
        }

        @org.testng.annotations.BeforeClass
        public void beforeClass() {
            ApplicationContext fooContext = testManager.getConfiguredContext("foo");
            ApplicationContext barContext = testManager.getConfiguredContext("bar");
            ApplicationContext bazContext = testManager.getConfiguredContext("baz");

            assertThat(fooContext, is(notNullValue()));
            assertThat(barContext, is(notNullValue()));
            assertThat(bazContext, is(notNullValue()));

            assertThat(fooContext, is(instanceOf(ConfigurableApplicationContext.class)));
            assertThat(barContext, is(instanceOf(ConfigurableApplicationContext.class)));
            assertThat(bazContext, is(instanceOf(ConfigurableApplicationContext.class)));

            // verify all contexts are active(not closed)
            assertThat(((ConfigurableApplicationContext) fooContext).isActive(), is(true));
            assertThat(((ConfigurableApplicationContext) barContext).isActive(), is(true));
            assertThat(((ConfigurableApplicationContext) bazContext).isActive(), is(true));
        }


        @org.testng.annotations.Test
        public void testA() {
            // do nothing
        }

        // TestNG annotation order:
        //   parent @BeforeClass, child @BeforeClass, child @AfterClass, parent @AfterClass
        // add "dependsOnMethods" so that parent @AfterClass get called before this method.
        @org.testng.annotations.AfterClass(dependsOnMethods = "publishAfterClassEvent")
        public void afterClass() {
            ApplicationContext fooContext = testManager.getConfiguredContext("foo");
            ApplicationContext barContext = testManager.getConfiguredContext("bar");
            ApplicationContext bazContext = testManager.getConfiguredContext("baz");

            assertThat(fooContext, is(notNullValue()));
            assertThat(barContext, is(notNullValue()));
            assertThat(bazContext, is(notNullValue()));

            // verify app context closed(inactive)
            assertThat(((ConfigurableApplicationContext) fooContext).isActive(), is(false));
            assertThat(((ConfigurableApplicationContext) barContext).isActive(), is(false));
            assertThat(((ConfigurableApplicationContext) bazContext).isActive(), is(true));
        }

    }

    @ConfiguredContextDefinition
    public static class TestConfiguration {

        @ConfiguredContext(name = "foo", classes = AppContext.class)
        public void fooContext() {
        }

        @ConfiguredContext(name = "bar", classes = AppContext.class)
        public void barContext() {
        }

        @ConfiguredContext(name = "baz", classes = AppContext.class)
        public void bazContext() {
        }
    }

    @Configuration
    public static class AppContext {
        @Bean
        public String bean() {
            return "bean";
        }
    }
}
