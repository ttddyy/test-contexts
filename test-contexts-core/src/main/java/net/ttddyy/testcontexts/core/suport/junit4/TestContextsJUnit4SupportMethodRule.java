package net.ttddyy.testcontexts.core.suport.junit4;

import net.ttddyy.testcontexts.core.*;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * JUnit method level rule.
 * <p/>
 * Due to the junit @Rule API, it has to pass the test instance to the rule.
 * https://github.com/KentBeck/junit/issues/351
 *
 * @author Tadaya Tsuyukubo
 */
public class TestContextsJUnit4SupportMethodRule extends TestWatcher {

    private Object testInstance;

    public TestContextsJUnit4SupportMethodRule(Object testInstance) {
        this.testInstance = testInstance;
    }

    @Override
    protected void starting(Description description) {
        final TestManager testManager = TestManagerHolder.get();

        // junit creates test instance per test method, so here always creates a new runtime context
        // TODO: close & clear runtime context when test finishes.
        ApplicationContext runtimeContext = testManager.createOrGetRuntimeContext(testInstance, RuntimeContextMetaInfo.TestType.JUNIT4);

        publishEvent(description, TestLifecycleEventType.BEFORE_METHOD);
    }

    @Override
    protected void finished(Description description) {
        publishEvent(description, TestLifecycleEventType.AFTER_METHOD);
    }


    private void publishEvent(Description description, TestLifecycleEventType eventType) {
        final Class<?> testClass = description.getTestClass();

        // for parameterized test, method name ends with index
        //   example: "method[0]", "method[1]"
        String testMethodName = description.getMethodName();
        if (PropertyAccessorUtils.isNestedOrIndexedProperty(testMethodName)) {
            testMethodName = PropertyAccessorUtils.getPropertyName(testMethodName);
        }
        final Method testMethod = ReflectionUtils.findMethod(testClass, testMethodName);

        final TestManager testManager = TestManagerHolder.get();
        final ApplicationContext runtimeContext = testManager.getRuntimeContext(testInstance);

        final TestEventStatus eventStatus = new TestEventStatus(testClass, testMethod);
        final TestLifecycleEvent event = new TestLifecycleEvent(runtimeContext, eventType, eventStatus);
        runtimeContext.publishEvent(event);
    }

}
