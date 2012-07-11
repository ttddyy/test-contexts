package net.ttddyy.testcontexts.core;

import org.springframework.context.ApplicationListener;

import java.lang.reflect.Method;

/**
 * Internal representation of {@link ConfiguredContext}.
 *
 * @author Tadaya Tsuyukubo
 */
public class ParsedConfiguredContextDefinition {

    public static enum ContextCreationStrategy {
        BY_CONFIG_FILE, BY_ANNOTATED_CLASS, BY_METHOD_INVOCATION
    }

    private String contextName;
    private String parentContextName;
    private String[] childContextNames = new String[0];

    private Class<? extends ApplicationListener<? extends TestLifecycleEvent>>[] listeners = new Class[0];
    private String[] profiles = new String[0];

    private ContextCreationStrategy contextCreation;
    private Class<?>[] definitionClasses = new Class[0]; // set BY_ANNOTATED_CLASS
    private String[] definitionFiles = new String[0];  // set BY_CONFIG_FILE
    private Method definitionMethod; // set BY_METHOD_INVOCATION

    private int order;


    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getParentContextName() {
        return parentContextName;
    }

    public void setParentContextName(String parentContextName) {
        this.parentContextName = parentContextName;
    }

    public String[] getChildContextNames() {
        return childContextNames;
    }

    public void setChildContextNames(String[] childContextNames) {
        this.childContextNames = childContextNames;
    }

    public Class<? extends ApplicationListener<? extends TestLifecycleEvent>>[] getListeners() {
        return listeners;
    }

    public void setListeners(Class<? extends ApplicationListener<? extends TestLifecycleEvent>>[] listeners) {
        this.listeners = listeners;
    }

    public String[] getProfiles() {
        return profiles;
    }

    public void setProfiles(String[] profiles) {
        this.profiles = profiles;
    }

    public ContextCreationStrategy getContextCreationStrategy() {
        return contextCreation;
    }

    public void setContextCreation(ContextCreationStrategy contextCreation) {
        this.contextCreation = contextCreation;
    }

    public Class<?>[] getDefinitionClasses() {
        return definitionClasses;
    }

    public void setDefinitionClasses(Class<?>[] definitionClasses) {
        this.definitionClasses = definitionClasses;
    }

    public String[] getDefinitionFiles() {
        return definitionFiles;
    }

    public void setDefinitionFiles(String[] definitionFiles) {
        this.definitionFiles = definitionFiles;
    }

    public Method getDefinitionMethod() {
        return definitionMethod;
    }

    public void setDefinitionMethod(Method definitionMethod) {
        this.definitionMethod = definitionMethod;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
