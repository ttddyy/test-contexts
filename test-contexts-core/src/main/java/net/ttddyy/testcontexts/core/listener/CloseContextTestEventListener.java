package net.ttddyy.testcontexts.core.listener;

import net.ttddyy.testcontexts.core.TestLifecycleEvent;
import net.ttddyy.testcontexts.core.TestLifecycleEventListenerAdapter;
import net.ttddyy.testcontexts.core.TestManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.Resource;

/**
 * Close runtime-context and specified configured-contexts at the end of the annotated test class.
 *
 * @author Tadaya Tsuyukubo
 */
public class CloseContextTestEventListener extends TestLifecycleEventListenerAdapter {

    private TestManager testManager;


    @Override
    protected void onAfterClass(TestLifecycleEvent event) {
        final ApplicationContext runtimeContext = event.getApplicationContext();
        final Class<?> testClass = event.getEventStatus().getTestInstance().getClass();

        final CloseContext closeContext = AnnotationUtils.findAnnotation(testClass, CloseContext.class);
        if (closeContext == null) {
            return; // not annotated
        }

        // close specified configured contexts
        for (String contextName : closeContext.contexts()) {
            final ApplicationContext applicationContext = testManager.getConfiguredContext(contextName);
            if (applicationContext != null && applicationContext instanceof ConfigurableApplicationContext) {
                ((ConfigurableApplicationContext) applicationContext).close();
            }
        }

        // close runtime context
        if (runtimeContext instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) runtimeContext).close();
        }

    }

    @Resource
    public void setTestManager(TestManager testManager) {
        this.testManager = testManager;
    }

}
