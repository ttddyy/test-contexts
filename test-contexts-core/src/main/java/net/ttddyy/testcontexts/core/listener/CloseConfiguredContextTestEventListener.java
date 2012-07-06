package net.ttddyy.testcontexts.core.listener;

import net.ttddyy.testcontexts.core.ConfiguredContextUtils;
import net.ttddyy.testcontexts.core.TestLifecycleEvent;
import net.ttddyy.testcontexts.core.TestLifecycleEventListenerAdapter;
import net.ttddyy.testcontexts.core.TestManager;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.Resource;

/**
 * Close runtime-context and specified configured-contexts at the end of the annotated test class.
 *
 * @author Tadaya Tsuyukubo
 */
public class CloseConfiguredContextTestEventListener extends TestLifecycleEventListenerAdapter {

    private TestManager testManager;


    @Override
    protected void onAfterClass(TestLifecycleEvent event) {
        final Class<?> testClass = event.getEventStatus().getTestClass();

        final CloseContext closeContext = AnnotationUtils.findAnnotation(testClass, CloseContext.class);
        if (closeContext == null) {
            return; // not annotated
        }

        // close specified configured contexts
        for (String contextName : closeContext.contexts()) {
            final ApplicationContext applicationContext = testManager.getConfiguredContext(contextName);
            ConfiguredContextUtils.close(applicationContext);
        }

    }

    @Resource
    public void setTestManager(TestManager testManager) {
        this.testManager = testManager;
    }

}
