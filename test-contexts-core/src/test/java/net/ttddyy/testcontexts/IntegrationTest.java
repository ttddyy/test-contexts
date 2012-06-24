package net.ttddyy.testcontexts;

import net.ttddyy.testcontexts.core.*;
import net.ttddyy.testcontexts.core.listener.CloseContextTestEventListener;
import net.ttddyy.testcontexts.core.suport.testng.AbstractTestNGSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testng.annotations.Test;

import javax.annotation.Resource;

import static org.testng.Assert.assertEquals;

/**
 * @author Tadaya Tsuyukubo
 */
@TestConfig(context = "contextBar", listeners = CloseContextTestEventListener.class)
public class IntegrationTest extends AbstractTestNGSupport {

    @Resource
    public String foo;

    @Autowired
    public String bar;

    static {
//        configClasses.add(IntegrationTestConfig.class);
    }
//    public IntegrationTest() {
//        testManager = new DefaultTestManager();
//
//        Class<?>[] configuredClasses = new Class<?>[]{IntegrationTestConfig.class};
//        testManager.prepareConfiguredContext(configuredClasses);
//        testManager.getOrCreateTestApplicationContext(this);
//
//    }

    @Test
    public void test() {
        TestManager contextManager = TestManagerHolder.get();
        if (contextManager != null) {
            contextManager.clear();
        }

//        assertEquals(foo, "FOO-BAR");
//        assertEquals(bar, "BAR");
    }

    @ConfiguredContextDefinition
    private static class IntegrationTestConfig {

        @ConfiguredContext(
                name = "contextFoo",
                classes = ConfigFoo.class
        )
        public void configFoo() {
        }

        @ConfiguredContext(
                name = "contextBar",
                classes = ConfigBar.class,
                parent = "contextFoo"
        )
        public void configBar() {
        }
    }

    @Configuration
    public static class ConfigFoo {
        @Bean
        public String foo() {
            return "FOO";
        }
    }

    @Configuration
    public static class ConfigBar {
        @Bean
        public String foo() {
            return "FOO-BAR";
        }

        @Bean
        public String bar() {
            return "BAR";
        }
    }


}
