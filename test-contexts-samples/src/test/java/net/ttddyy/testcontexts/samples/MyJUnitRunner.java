package net.ttddyy.testcontexts.samples;

import net.ttddyy.testcontexts.core.suport.junit4.TestContextsJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Tadaya Tsuyukubo
 */
public class MyJUnitRunner extends TestContextsJUnit4ClassRunner {

    public MyJUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    public Set<Class<?>> getConfigurationDefinitionClasses() {
        Set<Class<?>> configs = new HashSet<Class<?>>();
        configs.add(MyTestConfig.class);
        return configs;
    }
}
