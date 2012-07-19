package net.ttddyy.testcontexts.samples;

import net.ttddyy.testcontexts.core.ConfiguredContext;
import net.ttddyy.testcontexts.core.ConfiguredContextDefinition;

/**
 * @author Tadaya Tsuyukubo
 */

@ConfiguredContextDefinition
public class MyTestConfig {

    @ConfiguredContext(name = "dao", locations = "classpath:dao-context.xml")
    public void foo() {

    }

    @ConfiguredContext(name = "serviceWithDao", locations = "classpath:service-context.xml", parent = "dao")
    public void service() {
    }

    @ConfiguredContext(name = "mockDao", locations = "classpath:dao-mock-context.xml")
    public void mockDao() {

    }

    @ConfiguredContext(name = "serviceWithMock", locations = "classpath:service-context.xml", parent = "mockDao")
    public void serviceWithMock() {
    }
}
