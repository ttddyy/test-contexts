package net.ttddyy.testcontexts.core.suport.junit4;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.List;
import java.util.Set;

/**
 * JUnit Runner implementation for Test Contexts.
 *
 * @author Tadaya Tsuyukubo
 */
public abstract class TestContextsJUnit4ClassRunner extends BlockJUnit4ClassRunner {

    public abstract Set<Class<?>> getConfigurationDefinitionClasses();

    public TestContextsJUnit4ClassRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected List<TestRule> classRules() {

        final TestContextsJUnit4Rules.ContextDefinitionRetrievalStrategy strategy = new TestContextsJUnit4Rules.ContextDefinitionRetrievalStrategy() {
            @Override
            public Class<?>[] getClasses(Statement base, Description description) {
                final Set<Class<?>> classes = getConfigurationDefinitionClasses();
                return classes.toArray(new Class<?>[classes.size()]);
            }
        };

        final TestRule initializationRule = TestContextsJUnit4Rules.getInitializeRule(strategy);


        final List<TestRule> rules = super.classRules();
        rules.add(0, initializationRule);
        rules.add(0, TestContextsJUnit4Rules.CLASS_RULE);
        return rules;
    }

    @Override
    protected List<TestRule> getTestRules(Object target) {
        final List<TestRule> rules = super.getTestRules(target);
        rules.add(TestContextsJUnit4Rules.createMethodRule(target));
        return rules;
    }

}
