package net.ttddyy.testcontexts.core.listener;

import net.ttddyy.testcontexts.core.*;
import org.springframework.context.ApplicationContext;

/**
 * @author Tadaya Tsuyukubo
 */
public class CloseRuntimeContextTestEventListener extends TestLifecycleEventListenerAdapter {

    @Override
    protected void onAfterMethod(TestLifecycleEvent event) {
        final ApplicationContext context = event.getApplicationContext();

        // JUnit create test instance per test method.
        // close runtime context on after method.
        if (!RuntimeContextUtils.isRuntimeContext(context)) {
            return;
        }

        final RuntimeContextMetaInfo metaInfo = (RuntimeContextMetaInfo) ConfiguredContextUtils.getMetaInfo(context);
        if (RuntimeContextMetaInfo.TestType.JUNIT4.equals((metaInfo).getTestType())) {
            RuntimeContextUtils.close(context); // close runtime context
        }
    }

    @Override
    protected void onAfterClass(TestLifecycleEvent event) {
        final ApplicationContext context = event.getApplicationContext();

        if (!RuntimeContextUtils.isRuntimeContext(context)) {
            return;
        }

        // close runtime context on after class if TestNG.
        final RuntimeContextMetaInfo metaInfo = (RuntimeContextMetaInfo) ConfiguredContextUtils.getMetaInfo(context);
        if (RuntimeContextMetaInfo.TestType.TESTNG.equals(metaInfo.getTestType())) {
            RuntimeContextUtils.close(context); // close runtime context
        }
    }


}
