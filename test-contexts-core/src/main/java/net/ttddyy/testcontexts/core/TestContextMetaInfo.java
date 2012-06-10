package net.ttddyy.testcontexts.core;

/**
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
