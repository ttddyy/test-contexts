package net.ttddyy.testcontexts.core;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Tadaya Tsuyukubo
 */
public class ConfiguredContextUtilsTests {

    @Test
    public void testGetMetaInfo() {
        TestContextMetaInfo metaInfo = new TestContextMetaInfo();

        // prepare app context
        GenericApplicationContext applicationContext = new GenericApplicationContext();
        applicationContext.getBeanFactory().registerSingleton(TestManager.METAINFO_BEAN_NAME, metaInfo);
        applicationContext.refresh();

        TestContextMetaInfo result = ConfiguredContextUtils.getMetaInfo(applicationContext);
        assertThat(result, is(notNullValue()));
        assertThat(result, sameInstance(metaInfo));
    }

    @Test
    public void testCreateConfiguredContextFromDefinition() {

        StaticApplicationContext parentContext = new StaticApplicationContext();
        parentContext.refresh();

        ParsedConfiguredContextDefinition definition = new ParsedConfiguredContextDefinition();
        definition.setContextCreation(ParsedConfiguredContextDefinition.ContextCreationStrategy.BY_ANNOTATED_CLASS);
        definition.setContextName("foo");
        definition.setDefinitionClasses(new Class[]{DummyConfig.class});

        ApplicationContext context = ConfiguredContextUtils.createConfiguredContext(definition, parentContext);

        assertThat(context, is(notNullValue()));
        assertThat(context.getParent(), is(sameInstance((ApplicationContext) parentContext)));
        assertThat(context.containsBean("foo"), is(true));
        assertThat(context.getBean("foo"), instanceOf(String.class));
        assertThat((String) context.getBean("foo"), is("FOO"));

        // verify metainfo bean
        assertThat(context.containsBean(TestManager.METAINFO_BEAN_NAME), is(true));
        assertThat(context.getBean(TestManager.METAINFO_BEAN_NAME), instanceOf(TestContextMetaInfo.class));
        TestContextMetaInfo metaInfo = context.getBean(TestManager.METAINFO_BEAN_NAME, TestContextMetaInfo.class);
        assertThat(metaInfo.getDefinition(), is(sameInstance(definition)));
        assertThat(metaInfo.getContextType(), is(ContextType.CONFIGURED));

    }

    @Configuration
    static class DummyConfig {
        @Bean
        public String foo() {
            return "FOO";
        }
    }

    @Test
    public void testCreateConfiguredContextFromMethod() {

        StaticApplicationContext parentContext = new StaticApplicationContext();
        parentContext.refresh();

        Method definitionMethod = ReflectionUtils.findMethod(getClass(), "createContextByMethod", ApplicationContext.class);

        ParsedConfiguredContextDefinition definition = new ParsedConfiguredContextDefinition();
        definition.setContextCreation(ParsedConfiguredContextDefinition.ContextCreationStrategy.BY_METHOD_INVOCATION);
        definition.setContextName("foo");
        definition.setDefinitionMethod(definitionMethod);

        ApplicationContext context = ConfiguredContextUtils.createConfiguredContext(definition, parentContext);

        assertThat(context, is(notNullValue()));
        assertThat(context.getParent(), is(sameInstance((ApplicationContext) parentContext)));
        assertThat(context.containsBean("foo"), is(true));
        assertThat(context.getBean("foo"), instanceOf(String.class));
        assertThat((String) context.getBean("foo"), is("FOO"));

        // verify metainfo bean
        assertThat(context.containsBean(TestManager.METAINFO_BEAN_NAME), is(true));
        assertThat(context.getBean(TestManager.METAINFO_BEAN_NAME), instanceOf(TestContextMetaInfo.class));
        TestContextMetaInfo metaInfo = context.getBean(TestManager.METAINFO_BEAN_NAME, TestContextMetaInfo.class);
        assertThat(metaInfo.getDefinition(), is(sameInstance(definition)));
        assertThat(metaInfo.getContextType(), is(ContextType.CONFIGURED));

    }

    public ApplicationContext createContextByMethod(ApplicationContext parentContext) {
        GenericApplicationContext context = new GenericApplicationContext();
        context.setParent(parentContext);
        new AnnotatedBeanDefinitionReader(context).register(DummyConfig.class);
        context.refresh();
        return context;
    }
}
