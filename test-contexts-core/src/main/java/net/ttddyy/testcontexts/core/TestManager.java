package net.ttddyy.testcontexts.core;

import org.springframework.context.ApplicationContext;

import java.util.Set;

/**
 * @author Tadaya Tsuyukubo
 */
public interface TestManager {

    // TODO: move constants to appopriate place
    static final String METAINFO_BEAN_NAME = "testContextsMetaInfo";
    static final String RUNTIME_CONTEXT_TESTBEAN_NAME = "runtime_testbean";

    ApplicationContext getFrameworkContext();

    boolean isConfiguredContextsInitialized();

    void prepareConfiguredContext(Class<?>[] configuredClasses);

    ApplicationContext getConfiguredContext(String contextName);

    void autoWire(String contextName, Object testInstance);

    ApplicationContext createRuntimeContext(Object testInstance);

    Set<ApplicationContext> getChildConfiguredContexts(String contextName);

    void refreshConfiguredContexts(String... contextNames);

}