package net.ttddyy.testcontexts.core.suport.junit4;

import net.ttddyy.testcontexts.core.TestManagerHolder;
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

        final TestRule classRule = new TestContextsJUnit4SupportClassRule(getConfigurationDefinitionClasses());

        final List<TestRule> rules = super.classRules();
        rules.add(0, classRule);
        return rules;
    }

    @Override
    protected List<TestRule> getTestRules(Object target) {
        final List<TestRule> rules = super.getTestRules(target);
        rules.add(new TestContextsJUnit4SupportMethodRule(target));
        return rules;
    }

}
