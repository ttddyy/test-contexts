package net.ttddyy.testcontexts.core.listener;

import net.ttddyy.testcontexts.core.*;
import org.springframework.context.ApplicationContext;

/**
 * @author Tadaya Tsuyukubo
 */
public class CloseRuntimeContextTestEventListener extends TestLifecycleEventListenerAdapter {

    @Override
    protected void onAfterMethod(TestLifecycleEvent event) {
        final ApplicationContext runtimeContext = event.getApplicationContext();

        // JUnit create test instance per test method.
        // close runtime context on after method.

        final RuntimeContextMetaInfo metaInfo = (RuntimeContextMetaInfo) ConfiguredContextUtils.getMetaInfo(runtimeContext);
        if (RuntimeContextMetaInfo.TestType.JUNIT4.equals(metaInfo.getTestType())) {
            RuntimeContextUtils.close(runtimeContext); // close runtime context
        }
    }

    @Override
    protected void onAfterClass(TestLifecycleEvent event) {
        final ApplicationContext runtimeContext = event.getApplicationContext();

        // close runtime context on after class if TestNG.
        final RuntimeContextMetaInfo metaInfo = (RuntimeContextMetaInfo) ConfiguredContextUtils.getMetaInfo(runtimeContext);
        if (RuntimeContextMetaInfo.TestType.TESTNG.equals(metaInfo.getTestType())) {
            RuntimeContextUtils.close(runtimeContext); // close runtime context
        }
    }


}
