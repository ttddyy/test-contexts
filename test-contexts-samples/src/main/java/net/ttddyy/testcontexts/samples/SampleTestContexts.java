package net.ttddyy.testcontexts.samples;

import net.ttddyy.testcontexts.core.ConfiguredContext;
import net.ttddyy.testcontexts.core.ConfiguredContextDefinition;
import org.springframework.context.ApplicationContext;

/**
 * @author Tadaya Tsuyukubo
 */
@ConfiguredContextDefinition
public class SampleTestContexts {

    // TODO: how to specify default one?  name="default" or param default=true?

    @ConfiguredContext(
            name = "DEFAULT",
            locations = "abc.xml"
    )
    public void base() {

    }

    @ConfiguredContext(
            name = "something",
            parent = "base",
            locations = "abc.xml"
    )
    public void something() {

    }

    // support when return is app context
    public ApplicationContext abc(ApplicationContext parent) {
        return null;
    }

}
