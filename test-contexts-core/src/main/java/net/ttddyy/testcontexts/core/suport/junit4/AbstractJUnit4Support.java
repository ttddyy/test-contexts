package net.ttddyy.testcontexts.core.suport.junit4;

import net.ttddyy.testcontexts.core.SpecifyContextDefinitionClasses;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * Parent class for junit test classes.
 *
 *
 * @author Tadaya Tsuyukubo
 */

public abstract class AbstractJUnit4Support {

    @ClassRule
    public static TestRule CLASS_RULE;

    @Rule
    public TestRule methodRule = TestContextsJUnit4Rules.createMethodRule(this);

    static {
        final TestContextsJUnit4Rules.ContextDefinitionRetrievalStrategy strategy = new TestContextsJUnit4Rules.ContextDefinitionRetrievalStrategy() {
            @Override
            public Class<?>[] getClasses() {
                final SpecifyContextDefinitionClasses annotation =
                        AnnotationUtils.findAnnotation(this.getClass(), SpecifyContextDefinitionClasses.class);
                return annotation.classes();
            }
        };

        final TestRule initializeRule = TestContextsJUnit4Rules.getInitializeRule(strategy);
        final TestRule classWatcherRule = TestContextsJUnit4Rules.CLASS_RULE;

        // ordered rule
        CLASS_RULE = RuleChain.outerRule(initializeRule).around(classWatcherRule);
    }

}
