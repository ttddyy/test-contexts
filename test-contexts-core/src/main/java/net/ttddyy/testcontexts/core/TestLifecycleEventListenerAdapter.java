package net.ttddyy.testcontexts.core;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

/**
 * @author Tadaya Tsuyukubo
 */
public class TestLifecycleEventListenerAdapter implements ApplicationListener<TestLifecycleEvent> {
    public void onApplicationEvent(TestLifecycleEvent event) {

        final TestLifecycleEventType eventType = event.getEventType();

        onEvent(event, eventType);

        switch (eventType) {
            case BEFORE_ALL_TESTS:
                onBeforeAllTests(event);
                break;
            case AFTER_ALL_TESTS:
                onAfterAllTests(event);
                break;
            case BEFORE_SUITE:
                onBeforeSuite(event);
                break;
            case AFTER_SUITE:
                onAfterSuite(event);
                break;
            case BEFORE_CLASS:
                onBeforeClass(event);
                break;
            case AFTER_CLASS:
                onAfterClass(event);
                break;
            case BEFORE_METHOD:
                onBeforeMethod(event);
                break;
            case AFTER_METHOD:
                onAfterMethod(event);
                break;
            default:
                throw new TestContextException("Not supported event type: " + eventType);
        }
    }

    protected void onEvent(TestLifecycleEvent event, TestLifecycleEventType eventType) {
    }

    protected void onBeforeAllTests(TestLifecycleEvent event) {
    }

    protected void onBeforeSuite(TestLifecycleEvent event) {
    }

    protected void onBeforeClass(TestLifecycleEvent event) {
    }

    protected void onBeforeMethod(TestLifecycleEvent event) {
    }

    protected void onAfterAllTests(TestLifecycleEvent event) {
    }

    protected void onAfterSuite(TestLifecycleEvent event) {
    }

    protected void onAfterClass(TestLifecycleEvent event) {
    }

    protected void onAfterMethod(TestLifecycleEvent event) {
    }
}
