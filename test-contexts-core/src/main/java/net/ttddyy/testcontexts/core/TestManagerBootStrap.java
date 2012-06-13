package net.ttddyy.testcontexts.core;

import net.ttddyy.testcontexts.core.listener.CloseContextTestEventListener;
import net.ttddyy.testcontexts.core.listener.RefreshContextTestEventListener;
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
                new AnnotationConfigApplicationContext(FrameworkContextConfiguration.class);
        applicationContext.setDisplayName(ROOT_CONTEXT_NAME);

        final TestManager testManager = applicationContext.getBean(TestManager.class);
        TestManagerHolder.set(testManager);
    }

    /**
     * Framework Context(Root) application context definition.
     */
    @Configuration
    public static class FrameworkContextConfiguration {

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

        @Bean
        public CloseContextTestEventListener closeContextTestEventListener(){
            return new CloseContextTestEventListener();
        }

        @Bean
        public RefreshContextTestEventListener refreshContextTestEventListener(){
            return new RefreshContextTestEventListener();
        }
    }
}
