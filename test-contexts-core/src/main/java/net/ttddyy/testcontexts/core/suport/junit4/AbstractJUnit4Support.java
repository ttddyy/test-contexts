package net.ttddyy.testcontexts.core.suport.junit4;

import net.ttddyy.testcontexts.core.SpecifyContextDefinitionClasses;
import net.ttddyy.testcontexts.core.TestManagerHolder;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Set;

/**
 * Parent class for junit test classes.
 * <p/>
 * Child class needs to specify context definition classes using {@link @SpecifyContextDefinitionClasses} annotation.
 *
 * @author Tadaya Tsuyukubo
 */

public abstract class AbstractJUnit4Support {

    @ClassRule
    public static TestRule classRule = new TestContextsJUnit4SupportClassRule(new TestContextsJUnit4SupportClassRule.ContextDefinitionRetrievalStrategy() {

        // runtime evaluation to retrieve context definition classes from @SpecifyContextDefinitionClasses annotation
        @Override
        public Class<?>[] getClasses(Statement base, Description description) {
            final SpecifyContextDefinitionClasses annotation =
                    AnnotationUtils.findAnnotation(description.getTestClass(), SpecifyContextDefinitionClasses.class);
            return annotation.classes();
        }
    });

    @Rule
    public TestRule methodRule = new TestContextsJUnit4SupportMethodRule(this);


}
