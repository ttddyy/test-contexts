package net.ttddyy.testcontexts.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author Tadaya Tsuyukubo
 */
public class DefaultTestManager implements TestManager, ApplicationContextAware {

    // essentially this is the cache to hold application contexts by context-name
    private Map<String, ApplicationContext> configuredContextMap = new HashMap<String, ApplicationContext>();
    private Map<Object, ApplicationContext> runtimeContextMap = new HashMap<Object, ApplicationContext>();

    private ConfiguredContextDefinitionValidator definitionValidator = new ConfiguredContextDefinitionValidator();
    private ConfiguredContextDefinitionParser definitionParser = new ConfiguredContextDefinitionParser();

    private Class<?>[] contextDefinitions;

    private ApplicationContext frameworkContext;

    /**
     * reference to the root ApplicationContext which contains TestManager itself.
     *
     * @param applicationContext
     * @throws BeansException
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.frameworkContext = applicationContext;
    }

    public ApplicationContext getFrameworkContext() {
        return frameworkContext;
    }

    public boolean isConfiguredContextsInitialized() {
        return !ObjectUtils.isEmpty(contextDefinitions);
    }

    public synchronized void prepareConfiguredContext(Class<?>... configuredContextDefinitions) {

        // already initialized, then check
        if (!ObjectUtils.isEmpty(contextDefinitions)) {
            final Set<Class> existing = new HashSet<Class>(Arrays.asList(contextDefinitions));
            final Set<Class> newDefs = new HashSet<Class>(Arrays.asList(configuredContextDefinitions));

            final boolean isSame = ((existing.size() == newDefs.size()) && existing.containsAll(newDefs));
            if (!isSame) {
                final StringBuilder sb = new StringBuilder("[");
                sb.append(StringUtils.collectionToCommaDelimitedString(existing));
                sb.append("]");

                throw new TestContextException("Configured Contexts are already initialized with classes=" + sb.toString());
            }
            return; // already initialized with same definition classes, do nothing
        }

        this.contextDefinitions = configuredContextDefinitions;

        // validate the configuration
        definitionValidator.validate(contextDefinitions);

        final List<ParsedConfiguredContextDefinition> sortedDefinitions = definitionParser.parse(contextDefinitions);

        for (ParsedConfiguredContextDefinition definition : sortedDefinitions) {

            final String contextName = definition.getContextName();

            final ApplicationContext context = createConfiguredContext(definition);

            configuredContextMap.put(contextName, context);

        }

    }

    private ApplicationContext createConfiguredContext(ParsedConfiguredContextDefinition definition) {

        // resolve parent context
        final String parentContextName = definition.getParentContextName();
        final ApplicationContext parentContext = getResolvedParentApplicationContext(parentContextName);

        return ConfiguredContextUtils.createConfiguredContext(definition, parentContext);
    }

    private ApplicationContext getResolvedParentApplicationContext(String parentContextName) {
        final ApplicationContext parentContext;
        if (StringUtils.hasText(parentContextName)) {
            parentContext = configuredContextMap.get(parentContextName);
        } else {
            // when configuration has null parent, set the root application context.
            parentContext = frameworkContext;
        }
        return parentContext;
    }

    public ApplicationContext getConfiguredContext(String contextName) {
        return configuredContextMap.get(contextName);
    }


    public void findConfigurationClass() {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ConfiguredContextDefinition.class));

        for (BeanDefinition bd : scanner.findCandidateComponents("")) {

            ReflectionUtils.getUniqueDeclaredMethods(bd.getClass());
//            AnnotationUtils.findAnnotation()
//            ClassUtils.forName(bd.getBeanClassName(), getClass().getClassLoader());
            System.out.println(bd.getBeanClassName());
            System.out.println(bd.getClass());
        }

    }


    public void autoWire(String contextName, Object testInstance) {
        // TODO: move to listener?

        // autowiring to test instance
        ApplicationContext applicationContext = createRuntimeContext(contextName);

        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        beanFactory.autowireBeanProperties(testInstance, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
        beanFactory.initializeBean(testInstance, testInstance.getClass().getName());

    }

    public ApplicationContext createRuntimeContext(Object testInstance) {

        // parent context
        final TestConfig testConfig = RuntimeContextUtils.getTestConfigAnnotation(testInstance);
        final String parentContextName = testConfig.context();
        final ApplicationContext parentContext = getResolvedParentApplicationContext(parentContextName);

        final ApplicationContext runtimeContext = RuntimeContextUtils.createRuntimeContext(testInstance, parentContext);

        runtimeContextMap.put(testInstance, runtimeContext);

        return runtimeContext;

    }

    public ApplicationContext createOrGetRuntimeContext(Object testInstance) {
        final ApplicationContext applicationContext = getRuntimeContext(testInstance);
        if (applicationContext != null) {
            return applicationContext;
        }
        return createRuntimeContext(testInstance);
    }

    public ApplicationContext getRuntimeContext(Object testInstance) {
        return runtimeContextMap.get(testInstance);
    }


    public Set<ApplicationContext> getChildConfiguredContexts(String contextName) {

        final ApplicationContext context = getConfiguredContext(contextName);
        if (context == null) {
            return Collections.emptySet();
        }

        final String[] childContextNames = ConfiguredContextUtils.getMetaInfo(context).getDefinition().getChildContextNames();

        final Set<ApplicationContext> childContexts = new HashSet<ApplicationContext>(childContextNames.length);
        for (String childContextName : childContextNames) {
            final ApplicationContext childContext = getConfiguredContext(childContextName);
            if (context == null) {
                continue;
            }
            childContexts.add(childContext);
        }

        return childContexts;
    }

    public Set<ApplicationContext> getAllChildConfiguredContexts(String contextName) {

        final Set<ApplicationContext> contexts = new HashSet<ApplicationContext>();

        Set<ApplicationContext> children = getChildConfiguredContexts(contextName);
        for (ApplicationContext child : children) {
            final TestContextMetaInfo metaInfo = ConfiguredContextUtils.getMetaInfo(child);
            for (String childContextName : metaInfo.getDefinition().getChildContextNames()) {
                contexts.addAll(getAllChildConfiguredContexts(childContextName));
            }
        }
        return contexts;
    }

    public void refreshConfiguredContexts(String... contextNames) {

        // get contexts include all children
        final Set<ApplicationContext> configuredContexts = new HashSet<ApplicationContext>();
        for (String contextName : contextNames) {
            configuredContexts.add(getConfiguredContext(contextName)); // itself
            configuredContexts.addAll(getAllChildConfiguredContexts(contextName)); //children
        }

        // parent context comes first
        final SortedSet<ApplicationContext> sorted = getSortedContexts(configuredContexts);
        for (ApplicationContext applicationContext : sorted) {
            final TestContextMetaInfo metaInfo = ConfiguredContextUtils.getMetaInfo(applicationContext);
            final ParsedConfiguredContextDefinition definition = metaInfo.getDefinition();
            final String contextName = definition.getContextName();

            // close
            if (applicationContext instanceof ConfigurableApplicationContext) {
                ((ConfigurableApplicationContext) applicationContext).close();
            }
            // recreate
            final ApplicationContext context = createConfiguredContext(definition);
            configuredContextMap.put(contextName, context);

        }
    }

    private SortedSet<ApplicationContext> getSortedContexts(Set<ApplicationContext> contexts) {
        List<ApplicationContext> list = new ArrayList<ApplicationContext>(contexts);

        // parent comes first
        Collections.sort(list, new Comparator<ApplicationContext>() {
            @Override
            public int compare(ApplicationContext left, ApplicationContext right) {
                final TestContextMetaInfo leftMeta = ConfiguredContextUtils.getMetaInfo(left);
                final TestContextMetaInfo rightMeta = ConfiguredContextUtils.getMetaInfo(right);
                final Integer leftOrder = leftMeta.getDefinition().getOrder();
                final Integer rightOrder = rightMeta.getDefinition().getOrder();
                return leftOrder.compareTo(rightOrder);
            }
        });

        return new TreeSet<ApplicationContext>(list);
    }


}
