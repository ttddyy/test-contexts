package net.ttddyy.testcontexts.core.listener;

import net.ttddyy.testcontexts.core.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * Event listener to refresh specified configured-contexts and the runtime-context.
 *
 * @author Tadaya Tsuyukubo
 */
public class RefreshContextTestEventListener extends TestLifecycleEventListenerAdapter {

    private TestManager testManager;

    @Override
    protected void onAfterMethod(TestLifecycleEvent event) {
        final ApplicationContext runtimeContext = event.getApplicationContext();
        final TestEventStatus eventStatus = event.getEventStatus();
        final Class<?> testClass = eventStatus.getTestClass();
        final Method testMethod = eventStatus.getTestMethod();

        RefreshContext refreshContext = AnnotationUtils.findAnnotation(testMethod, RefreshContext.class);
        if (refreshContext == null) {
            refreshContext = AnnotationUtils.findAnnotation(testClass, RefreshContext.class);
            if (refreshContext == null) {
                return; // not annotated
            }

            final RefreshContext.ClassMode classMode = refreshContext.classMode();
            if (RefreshContext.ClassMode.AFTER_CLASS == classMode) {
                return;
            }
        }

        doRefresh(runtimeContext, refreshContext);

    }

    @Override
    protected void onAfterClass(TestLifecycleEvent event) {
        final ApplicationContext runtimeContext = event.getApplicationContext();
        final Class<?> testClass = event.getEventStatus().getTestClass();

        final RefreshContext refreshContext = AnnotationUtils.findAnnotation(testClass, RefreshContext.class);

        if (refreshContext == null || refreshContext.classMode() == RefreshContext.ClassMode.AFTER_EACH_TEST_METHOD) {
            return; // not annotated, or annotated but specified after method
        }

        doRefresh(runtimeContext, refreshContext);

    }


    private void doRefresh(ApplicationContext runtimeContext, RefreshContext refreshContext) {

        // contexts to refresh
        final String[] contextNames = refreshContext.contexts();
        if (ObjectUtils.isEmpty(contextNames)) {
            refreshRuntimeContext(runtimeContext);
            return;
        }

        // refresh specified configured contexts
        testManager.refreshConfiguredContexts(contextNames);

        // runtime context
        refreshRuntimeContext(runtimeContext);
    }


    private void refreshRuntimeContext(ApplicationContext runtimeContext) {

        final RuntimeContextMetaInfo metaInfo = (RuntimeContextMetaInfo) ConfiguredContextUtils.getMetaInfo(runtimeContext);

        // retrieve test instance before closing(close clears all registered beans).
        final Object testInstance = RuntimeContextUtils.getTestInstance(runtimeContext);
        if (runtimeContext instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) runtimeContext).close();
        }

        // re-create new runtime instance
        testManager.createRuntimeContext(testInstance, metaInfo.getTestType());

    }

    @Resource
    public void setTestManager(TestManager testManager) {
        this.testManager = testManager;
    }
}
