package net.ttddyy.testcontexts.core;

/**
 * Context meta info holder.
 * <p/>
 * Each {@link org.springframework.context.ApplicationContext} created by Test Contexts framework
 * contains this bean to keep the meta information.
 *
 * @author Tadaya Tsuyukubo
 */
public class TestContextMetaInfo {

    private ContextType contextType;
    private ParsedConfiguredContextDefinition definition;

    public ContextType getContextType() {
        return contextType;
    }

    public void setContextType(ContextType contextType) {
        this.contextType = contextType;
    }

    public ParsedConfiguredContextDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(ParsedConfiguredContextDefinition definition) {
        this.definition = definition;
    }
}
