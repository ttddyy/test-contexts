package net.ttddyy.testcontexts.core;

import net.ttddyy.testcontexts.core.listener.CloseConfiguredContextTestEventListener;
import net.ttddyy.testcontexts.core.listener.CloseRuntimeContextTestEventListener;
import net.ttddyy.testcontexts.core.listener.RefreshContextTestEventListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bootstrap class for the test-contexts framework.
 * <p/>
 * create the framework-context(root-context), and set the {@link TestManager} to the {@link TestManagerHolder}.
 *
 * @author Tadaya Tsuyukubo
 */
public class TestManagerBootStrap {

    private static final String FRAMEWORK_CONTEXT_NAME = "FrameworkContext";

    public void createRootContext() {
        final AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(FrameworkContextConfiguration.class);
        applicationContext.setDisplayName(FRAMEWORK_CONTEXT_NAME);

        final TestManager testManager = applicationContext.getBean(TestManager.class);
        TestManagerHolder.set(testManager);
    }

    /**
     * Framework Context(Root) definition.
     * <p/>
     * Java based spring application context definition.
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
        public CloseConfiguredContextTestEventListener closeConfiguredContextTestEventListener() {
            return new CloseConfiguredContextTestEventListener();
        }

        @Bean
        public CloseRuntimeContextTestEventListener closeRuntimeContextTestEventListener() {
            return new CloseRuntimeContextTestEventListener();
        }

        @Bean
        public RefreshContextTestEventListener refreshContextTestEventListener() {
            return new RefreshContextTestEventListener();
        }
    }
}
