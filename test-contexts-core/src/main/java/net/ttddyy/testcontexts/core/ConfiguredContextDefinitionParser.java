package net.ttddyy.testcontexts.core;

import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
public class ConfiguredContextDefinitionParser {

    public List<ParsedConfiguredContextDefinition> parse(Class<?>... configuredClasses) {

        // get @ConfiguredContext annotated methods
        final List<Method> configMethods = new ArrayList<Method>();
        configMethods.addAll(ConfiguredContextAnnotationUtils.getAnnotatedMethods(configuredClasses));

        final List<ParsedConfiguredContextDefinition> definitions = new ArrayList<ParsedConfiguredContextDefinition>();
        for (Method configMethod : configMethods) {
            definitions.add(createParsedDefinition(configMethod));
        }

        final List<ParsedConfiguredContextDefinition> sortedDefinitions = getSortedDefinitions(definitions);

        // update definition.childContextNames
        updateChildContextNames(sortedDefinitions);

        return sortedDefinitions;
    }

    private ParsedConfiguredContextDefinition createParsedDefinition(Method configuredMethod) {
        final ConfiguredContext configuredContext = AnnotationUtils.findAnnotation(configuredMethod, ConfiguredContext.class);

        final String contextName;
        if (StringUtils.hasText(configuredContext.name())) {
            contextName = configuredContext.name();
        } else {
            // use method name
            contextName = configuredMethod.getName();
        }

        final String parentContextName;
        if (StringUtils.hasText(configuredContext.parent())) {
            parentContextName = configuredContext.parent();
        } else {
            parentContextName = null;  // will use ROOT context
        }


        final ParsedConfiguredContextDefinition definition = new ParsedConfiguredContextDefinition();
        definition.setContextName(contextName);
        definition.setParentContextName(parentContextName);
        definition.setListeners(configuredContext.listeners());
        definition.setProfiles(configuredContext.profiles());


        final boolean isInvokeMethod = configuredMethod.getReturnType().isAssignableFrom(ApplicationContext.class);
        if (isInvokeMethod) {
            definition.setContextCreation(ParsedConfiguredContextDefinition.ContextCreationStrategy.BY_METHOD_INVOCATION);
            definition.setDefinitionMethod(configuredMethod);
        } else if (!ObjectUtils.isEmpty(configuredContext.locations())) {
            definition.setContextCreation(ParsedConfiguredContextDefinition.ContextCreationStrategy.BY_CONFIG_FILE);
            definition.setDefinitionFiles(configuredContext.locations());
        } else {
            definition.setContextCreation(ParsedConfiguredContextDefinition.ContextCreationStrategy.BY_ANNOTATED_CLASS);
            definition.setDefinitionClasses(configuredContext.classes());

        }

        return definition;
    }

    private void updateChildContextNames(List<ParsedConfiguredContextDefinition> definitions) {

        // create a map that represents: parent => list of child context names
        final MultiValueMap<String, String> childrenByParent = new LinkedMultiValueMap<String, String>();
        for (ParsedConfiguredContextDefinition definition : definitions) {
            final String contextName = definition.getContextName();
            final String parentContextName = definition.getParentContextName();

            if (parentContextName == null) {
                continue;
            }

            childrenByParent.add(parentContextName, contextName);
        }

        for (ParsedConfiguredContextDefinition definition : definitions) {
            final String contextName = definition.getContextName();
            final List<String> childContextNames = childrenByParent.get(contextName);

            if (!CollectionUtils.isEmpty(childContextNames)) {
                definition.setChildContextNames(childContextNames.toArray(new String[childContextNames.size()]));
            }
        }
    }

    private List<ParsedConfiguredContextDefinition> getSortedDefinitions(List<ParsedConfiguredContextDefinition> definitions) {

        // configs with no parent should be created first. parent first, then child.

        // TODO: refactor to better algorithm

        final List<ParsedConfiguredContextDefinition> list = new ArrayList<ParsedConfiguredContextDefinition>();
        for (ParsedConfiguredContextDefinition definition : definitions) {

            final String contextName = definition.getContextName();
            final String parentContextName = definition.getParentContextName();
            // with no parent
            if (parentContextName == null) {
                list.add(0, definition); // add to top
                continue;
            }

            final List<String> currentContextNames = getContextNames(list);
            final List<String> currentParentContextNames = getParentContextNames(list);

            if (currentParentContextNames.contains(contextName)) {
                // should be placed before the ones depending on
                final int index = currentParentContextNames.indexOf(contextName);
                list.add(index, definition);
                continue;
            }

            if (currentContextNames.contains(parentContextName)) {
                // should be placed after the parent one
                final int index = currentContextNames.indexOf(parentContextName);
                list.add(index + 1, definition);
                continue;
            }

            // place to the end
            list.add(definition);

        }

        // update context order info
        int order = 1;  // 0 is root
        for (ParsedConfiguredContextDefinition definition : definitions) {
            definition.setOrder(order);
            order++;
        }

        return list;
    }

    private List<String> getContextNames(List<ParsedConfiguredContextDefinition> definitions) {
        final List<String> list = new ArrayList<String>(definitions.size());
        for (ParsedConfiguredContextDefinition definition : definitions) {
            list.add(definition.getContextName());
        }
        return list;
    }

    private List<String> getParentContextNames(List<ParsedConfiguredContextDefinition> definitions) {
        final List<String> list = new ArrayList<String>(definitions.size());
        for (ParsedConfiguredContextDefinition definition : definitions) {
            list.add(definition.getParentContextName());
        }
        return list;
    }


}
