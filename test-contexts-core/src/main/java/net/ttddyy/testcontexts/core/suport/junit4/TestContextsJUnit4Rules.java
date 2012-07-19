package net.ttddyy.testcontexts.core.suport.junit4;

import net.ttddyy.testcontexts.core.*;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * JUnit rules for {@link AbstractJUnit4Support} and {@link TestContextsJUnit4Rules}.
 *
 * @author Tadaya Tsuyukubo
 */
public class TestContextsJUnit4Rules {

    public static interface ContextDefinitionRetrievalStrategy {
        Class<?>[] getClasses(Statement base, Description description);
    }

    public static TestRule getInitializeRule(final ContextDefinitionRetrievalStrategy strategy) {
        return new TestRule() {
            private boolean isCalled = false;

            @Override
            public Statement apply(Statement base, Description description) {

                if (isCalled) {
                    return base;
                }

                // initialize TestManager
                synchronized (TestManagerHolder.class) {

                    TestManager testManager = TestManagerHolder.get();
                    if (testManager == null) {
                        final TestManagerBootStrap bootStrap = new TestManagerBootStrap();
                        bootStrap.createRootContext();
                        testManager = TestManagerHolder.get();
                    }

                    // create configured contexts
                    if (!testManager.isConfiguredContextsInitialized()) {
                        final Class<?>[] configClasses = strategy.getClasses(base, description);
                        testManager.prepareConfiguredContext(configClasses);
                    }

                    isCalled = true;
                }

                return base;
            }
        };

    }

    public static final TestRule CLASS_RULE = new TestWatcher() {

        @Override
        protected void starting(Description description) {
            publishEvent(description, TestLifecycleEventType.BEFORE_CLASS);
        }

        @Override
        protected void finished(Description description) {
            publishEvent(description, TestLifecycleEventType.AFTER_CLASS);
        }

        private void publishEvent(Description description, TestLifecycleEventType eventType) {
            final Class<?> testClass = description.getTestClass();
            final TestConfig testConfig = AnnotationUtils.findAnnotation(testClass, TestConfig.class);
            final String parentContextName = testConfig.context();

            final TestManager testManager = TestManagerHolder.get();
            final ApplicationContext parentContext;
            if (StringUtils.hasText(parentContextName)) {
                parentContext = testManager.getConfiguredContext(parentContextName);
            } else {
                parentContext = testManager.getFrameworkContext();
            }

            final TestEventStatus eventStatus = new TestEventStatus(testClass, null);
            final TestLifecycleEvent event = new TestLifecycleEvent(parentContext, eventType, eventStatus);
            parentContext.publishEvent(event);
        }

    };


    public static TestRule createMethodRule(final Object testInstance) {

        return new TestWatcher() {

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
        };
    }

}
