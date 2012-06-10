package net.ttddyy.testcontexts.core;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Tadaya Tsuyukubo
 */
public class TestManagerBootStrap {

    private static final String ROOT_CONTEXT_NAME = "ROOT_TEST_CONTEXT";

    public void createRootContext() {
        final AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(RootTestContextConfiguration.class);
        applicationContext.setDisplayName(ROOT_CONTEXT_NAME);

        final TestManager testManager = applicationContext.getBean(TestManager.class);
        TestManagerHolder.set(testManager);
    }

    /**
     * Root application context definition.
     */
    @Configuration
    public static class RootTestContextConfiguration {

        @Bean
        public TestManager testManager() {
            return new DefaultTestManager();
        }

        @Bean
        public TestContextMetaInfo contextMetaInfo() {
            TestContextMetaInfo meta = new TestContextMetaInfo();
            meta.setContextType(ContextType.FRAMEWORK);
            // TODO: definition for root??
            return meta;
        }
    }
}
