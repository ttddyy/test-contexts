package net.ttddyy.testcontexts.core;

import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
public class ConfiguredContextDefinitionValidator {

    public void validate(Class<?>... configClasses) {

        if (ObjectUtils.isEmpty(configClasses)) {
            throw new TestContextException("no ConfiguredContextDefinition class is provided");
        }

        for (Class<?> configClass : configClasses) {
            final boolean isAnnotated = AnnotationUtils.isAnnotationDeclaredLocally(ConfiguredContextDefinition.class, configClass);
            if (!isAnnotated) {
                throw new TestContextException("No @ConfiguredContextDefinition annotated on " + configClass);
            }
        }

        // retrieve @ConfiguredContext annotations
        final List<Method> annotatedMethods = ConfiguredContextAnnotationUtils.getAnnotatedMethods(configClasses);
        if (annotatedMethods.isEmpty()) {
            return;
        }

        final List<String> contextNames = new ArrayList<String>();
        for (Method annotatedMethod : annotatedMethods) {

            final ConfiguredContext configuredContext = ConfiguredContextAnnotationUtils.getTestConfig(annotatedMethod);

            final boolean isInvokeMethod = annotatedMethod.getReturnType().isAssignableFrom(ApplicationContext.class);
            final boolean hasLocations = !ObjectUtils.isEmpty(configuredContext.locations());
            final boolean hasClasses = !ObjectUtils.isEmpty(configuredContext.classes());

            if (isInvokeMethod) {
                // cannot specify neither location nor class when method returns ApplicationContext
                if (hasLocations) {
                    throw new TestContextException("method returns ApplicationContext but xml locations are specified");
                }
                if (hasClasses) {
                    throw new TestContextException("method returns ApplicationContext but config classes are specified");
                }
            } else {
                if (!hasLocations && !hasClasses) {
                    throw new TestContextException("neither location nor classes are specified:" + configuredContext);
                }
                if (hasLocations && hasClasses) {
                    throw new TestContextException("both location and classes are specified:" + configuredContext);
                }
            }


            // TODO: check listeners?

            contextNames.add(configuredContext.name());
        }

        // check parent context exists
        for (Method annotatedMethod : annotatedMethods) {
            final ConfiguredContext configuredContext = ConfiguredContextAnnotationUtils.getTestConfig(annotatedMethod);

            final String parentName = configuredContext.parent();
            if (StringUtils.hasLength(parentName) && !contextNames.contains(parentName)) {
                throw new TestContextException("parent context doesn't exist:" + parentName);
            }
        }

        // check at least one configured context is a root(no parent context name).
        boolean existRootContextConfig = false;
        for (Method annotatedMethod : annotatedMethods) {
            final ConfiguredContext configuredContext = ConfiguredContextAnnotationUtils.getTestConfig(annotatedMethod);
            final String parentName = configuredContext.parent();
            if (!StringUtils.hasLength(parentName)) {
                existRootContextConfig = true;
                break;
            }
        }
        if (!existRootContextConfig) {
            throw new TestContextException("All configured context has parent.");
        }


        // check circular reference
        // TODO: update with better algorithm
        List<ConfiguredContext> configuredContexts = new ArrayList<ConfiguredContext>();
        for (Method annotatedMethod : annotatedMethods) {
            final ConfiguredContext configuredContext = ConfiguredContextAnnotationUtils.getTestConfig(annotatedMethod);
            configuredContexts.add(configuredContext);
        }
        final List<ConfiguredContext> rootConfigs = getRootContextConfigs(configuredContexts);
        final List<ConfiguredContext> checked = new ArrayList<ConfiguredContext>();
        for (ConfiguredContext rootConfig : rootConfigs) {
            checkChildren(rootConfig, configuredContexts, checked);
        }

    }

    private void checkChildren(ConfiguredContext config, List<ConfiguredContext> configs, List<ConfiguredContext> checked) {

        checked.add(config);

        final List<ConfiguredContext> children = new ArrayList<ConfiguredContext>();


        final String parentName = config.name();
        children.addAll(getContextConfigsWithParentName(parentName, configs));
        if (children.isEmpty()) {
            return;
        }
        for (ConfiguredContext child : children) {
            if (checked.contains(child)) {
                // circular reference detected
                final String contextName = child.name();
                final String msg = "Circular Reference Detected. More than one configuration is referencing context:" + contextName;
                throw new TestContextException(msg);
            }
            checkChildren(child, configs, checked);
        }
    }


    private List<ConfiguredContext> getRootContextConfigs(List<ConfiguredContext> configs) {
        final List<ConfiguredContext> result = new ArrayList<ConfiguredContext>();
        for (ConfiguredContext config : configs) {
            if (!StringUtils.hasLength(config.parent())) {
                result.add(config);
            }
        }
        return result;
    }

    private List<ConfiguredContext> getContextConfigsWithParentName(String parentName, List<ConfiguredContext> configs) {
        final List<ConfiguredContext> result = new ArrayList<ConfiguredContext>();
        for (ConfiguredContext config : configs) {
            if (config.parent().equals(parentName)) {
                result.add(config);
            }
        }
        return result;
    }

}
