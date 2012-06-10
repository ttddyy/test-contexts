package net.ttddyy.testcontexts.core;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
public class RuntimeContextUtils {

    public static Object getTestInstance(ApplicationContext runtimeContext) {
        final AutowireCapableBeanFactory beanFactory = runtimeContext.getAutowireCapableBeanFactory();
        return ((ConfigurableListableBeanFactory) beanFactory).getSingleton(TestManager.RUNTIME_CONTEXT_TESTBEAN_NAME);
    }

    public static TestConfig getTestConfigAnnotation(Object testInstance) {
        final Class<?> testClass = testInstance.getClass();
        return AnnotationUtils.findAnnotation(testClass, TestConfig.class);
    }

    public static ApplicationContext createRuntimeContext(Object testInstance, ApplicationContext parentContext) {

        final List<BeanDefinition> listenerBeanDefinitions = new ArrayList<BeanDefinition>();

        final TestConfig testConfig = getTestConfigAnnotation(testInstance);
        if (testConfig != null) {
            // runtime context level listeners
            final Class<? extends ApplicationListener<? extends TestLifecycleEvent>>[] listenerClasses = testConfig.listeners();
            for (Class<? extends ApplicationListener<? extends TestLifecycleEvent>> listenerClass : listenerClasses) {
                final AnnotatedBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(listenerClass);
                listenerBeanDefinitions.add(beanDefinition);
            }
        }

        // create runtime application context
        final GenericApplicationContext applicationContext = new GenericApplicationContext();
        applicationContext.setParent(parentContext);

        // test class may be using annotation. so register annotation processors
        AnnotationConfigUtils.registerAnnotationConfigProcessors(applicationContext);

        // register test-context-listeners
        for (BeanDefinition listenerBeanDefinition : listenerBeanDefinitions) {
            final String beanName = BeanDefinitionReaderUtils.generateBeanName(listenerBeanDefinition, applicationContext);
            applicationContext.registerBeanDefinition(beanName, listenerBeanDefinition);
        }
        applicationContext.refresh();
        applicationContext.registerShutdownHook();

        // autowire dependencies, call callbacks for initialize, then register testbean as a singleton bean
        final AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        beanFactory.autowireBeanProperties(testInstance, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
        beanFactory.initializeBean(testInstance, TestManager.RUNTIME_CONTEXT_TESTBEAN_NAME);
        ((ConfigurableListableBeanFactory) beanFactory).registerSingleton(TestManager.RUNTIME_CONTEXT_TESTBEAN_NAME, testInstance);

        return applicationContext;
    }

}
