package net.ttddyy.testcontexts.core;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Utils for ConfiguredContext application context instance.
 *
 * @author Tadaya Tsuyukubo
 */
public class ConfiguredContextUtils {

    public static TestContextMetaInfo getMetaInfo(ApplicationContext applicationContext) {
        final AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            return null;
        }

        final Object bean = ((ConfigurableListableBeanFactory) beanFactory).getSingleton(TestManager.METAINFO_BEAN_NAME);
        if (bean == null || !(bean instanceof TestContextMetaInfo)) {
            return null;
        }
        return (TestContextMetaInfo) bean;
    }

    public static ApplicationContext createConfiguredContext(ParsedConfiguredContextDefinition definition, ApplicationContext parentContext) {

        final ApplicationContext context;
        ParsedConfiguredContextDefinition.ContextCreationStrategy strategy = definition.getContextCreationStrategy();
        if (ParsedConfiguredContextDefinition.ContextCreationStrategy.BY_METHOD_INVOCATION.equals(strategy)) {
            context = createConfiguredContextFromMethod(definition, parentContext);
        } else {
            // by config classes or files
            context = createConfiguredContextFromAnnotation(definition, parentContext);
        }

        // create & register context meta info
        final TestContextMetaInfo metaInfo = new TestContextMetaInfo();
        metaInfo.setContextType(ContextType.CONFIGURED);
        metaInfo.setDefinition(definition);

        final AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
        ((ConfigurableListableBeanFactory) beanFactory).registerSingleton(TestManager.METAINFO_BEAN_NAME, metaInfo);


        return context;
    }

    /**
     * When method returns ApplicationContext, invoke the method and use the returned app context.
     *
     * @param definition
     * @param parentContext
     * @return
     */
    private static ApplicationContext createConfiguredContextFromMethod(ParsedConfiguredContextDefinition definition, ApplicationContext parentContext) {

        final Method method = definition.getDefinitionMethod();
        final boolean hasArguments = method.getParameterTypes().length > 0;
        final boolean isStatic = Modifier.isStrict(method.getModifiers());

        final ApplicationContext context;
        if (isStatic) {
            if (hasArguments) {
                context = (ApplicationContext) ReflectionUtils.invokeMethod(method, null, parentContext);
            } else {
                context = (ApplicationContext) ReflectionUtils.invokeMethod(method, null);
            }
        } else {

            final Class<?> clazz = method.getDeclaringClass();
            final Object instance = BeanUtils.instantiateClass(clazz);
            if (hasArguments) {
                context = (ApplicationContext) ReflectionUtils.invokeMethod(method, instance, parentContext);
            } else {
                context = (ApplicationContext) ReflectionUtils.invokeMethod(method, instance);
            }
        }

        // when context has empty display name
        if (context instanceof AbstractApplicationContext && !StringUtils.hasLength(context.getDisplayName())) {
            final String displayName;
            if (StringUtils.hasText(definition.getContextName())) {
                displayName = "ConfiguredContext-" + definition.getContextName();
            } else {
                displayName = "ConfiguredContext-" + method.getName();
            }
            ((AbstractApplicationContext) context).setDisplayName(displayName);
        }

        return context;

    }

    /**
     * Create an ApplicationContext based on the given @ConfiguredContext annotation
     *
     * @param definition
     * @param parent
     * @return
     */
    private static ApplicationContext createConfiguredContextFromAnnotation(ParsedConfiguredContextDefinition definition, ApplicationContext parent) {

        final GenericApplicationContext context = new GenericApplicationContext();
        context.setDisplayName("ConfiguredContext-" + definition.getContextName());
        context.setParent(parent);

        // profile
        context.getEnvironment().setActiveProfiles(definition.getProfiles());

        // bean definitions
        ParsedConfiguredContextDefinition.ContextCreationStrategy strategy = definition.getContextCreationStrategy();
        if (ParsedConfiguredContextDefinition.ContextCreationStrategy.BY_ANNOTATED_CLASS.equals(strategy)) {
            new AnnotatedBeanDefinitionReader(context).register(definition.getDefinitionClasses());
        } else {
            new XmlBeanDefinitionReader(context).loadBeanDefinitions(definition.getDefinitionFiles());
        }


        // register test lifecycle listeners if specified
        for (Class<? extends ApplicationListener<? extends TestLifecycleEvent>> listenerClass : definition.getListeners()) {
            final AnnotatedBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(listenerClass);
            final String beanName = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, context);
            context.registerBeanDefinition(beanName, beanDefinition);
        }

        context.refresh();

        return context;
    }
}
