package net.ttddyy.testcontexts.core.suport.junit4;

import net.ttddyy.testcontexts.core.*;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * @author Tadaya Tsuyukubo
 */
public class TestContextsJUnit4SupportClassRule implements TestRule {

    private static final TestRule EVENT_PUBLISHING_RULE = new TestWatcher() {

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

    private static class InitializingRule implements TestRule {
        private Class<?>[] configurationDefinitionClasses;
        private ContextDefinitionRetrievalStrategy strategy;
        private boolean isCalled = false;

        public InitializingRule(Class<?>... configurationDefinitionClasses) {
            this.configurationDefinitionClasses = configurationDefinitionClasses;
        }

        public InitializingRule(ContextDefinitionRetrievalStrategy strategy) {
            this.strategy = strategy;
        }

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

                // use strategy if not instantiated with definition classes
                Class<?>[] configClasses = this.configurationDefinitionClasses;
                if (ObjectUtils.isEmpty(configClasses)) {
                    configClasses = strategy.getClasses(base, description); // runtime evaluation
                }

                // create configured contexts
                if (!testManager.isConfiguredContextsInitialized()) {
                    testManager.prepareConfiguredContext(configClasses);
                }

                isCalled = true;
            }

            return base;
        }
    }

    public static interface ContextDefinitionRetrievalStrategy {
        Class<?>[] getClasses(Statement base, Description description);
    }


    private InitializingRule initializingRule;

    public TestContextsJUnit4SupportClassRule() {
    }

    public TestContextsJUnit4SupportClassRule(Class<?>... configurationDefinitionClasses) {
        this.initializingRule = new InitializingRule(configurationDefinitionClasses);
    }

    public TestContextsJUnit4SupportClassRule(Set<Class<?>> configurationDefinitionClasses) {
        final int size = configurationDefinitionClasses.size();
        final Class<?>[] configClasses = configurationDefinitionClasses.toArray(new Class<?>[size]);
        this.initializingRule = new InitializingRule(configClasses);
    }

    public TestContextsJUnit4SupportClassRule(ContextDefinitionRetrievalStrategy strategy) {
        this.initializingRule = new InitializingRule(strategy);
    }


    @Override
    public Statement apply(Statement base, Description description) {
        // ordered rule
        final RuleChain ruleChain = RuleChain.outerRule(initializingRule).around(EVENT_PUBLISHING_RULE);
        return ruleChain.apply(base, description);
    }
}
