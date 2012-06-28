package net.ttddyy.testcontexts.core;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Tadaya Tsuyukubo
 */
public class RuntimeContextUtilsTests {

    @Test
    public void testGetTestConfigAnnotation() {

        @TestConfig(context = "foo")
        class HasAnnotation {

        }
        class NoAnnotation {
        }

        // an instance with @TestConfig
        TestConfig config = RuntimeContextUtils.getTestConfigAnnotation(new HasAnnotation());
        assertThat(config, is(notNullValue()));
        assertThat(config.context(), is("foo"));

        // an instance without @TestConfig
        config = RuntimeContextUtils.getTestConfigAnnotation(new NoAnnotation());
        assertThat(config, is(nullValue()));
    }


    @Test
    public void testCreateRuntimeContexts() {

        @TestConfig(listeners = DummyListener.class)
        class FakeTest {

        }


        FakeTest fakeTest = new FakeTest();

        ApplicationContext context = RuntimeContextUtils.createRuntimeContext(fakeTest, null, null);

        assertThat(context, notNullValue());
        assertThat(context.getParent(), nullValue());

        assertThat(context.containsBean(TestManager.RUNTIME_CONTEXT_TESTBEAN_NAME), is(true));
        Object bean = context.getBean(TestManager.RUNTIME_CONTEXT_TESTBEAN_NAME);
        assertThat(bean, instanceOf(FakeTest.class));
        assertThat((FakeTest) bean, is(sameInstance(fakeTest)));

        // check listener
        String[] listenerBeanNames = context.getBeanNamesForType(DummyListener.class);
        assertThat(listenerBeanNames, arrayWithSize(1));

        // test for retrieving the test instance
        Object retrieved = RuntimeContextUtils.getTestInstance(context);
        assertThat(retrieved, instanceOf(FakeTest.class));
        assertThat((FakeTest) retrieved, is(sameInstance(fakeTest)));
    }

    static class DummyListener extends TestLifecycleEventListenerAdapter {
    }

    @Test
    public void testCreateRuntimeContextsFromNonAnnotatedInstance() {

        // test instance is not annotated as @TestConfig
        Object testInstance = new Object();

        ApplicationContext context = RuntimeContextUtils.createRuntimeContext(testInstance, null, null);

        assertThat(context, notNullValue());
        assertThat(context.getParent(), nullValue());
        assertThat(context.getDisplayName(), is("RuntimeContext-java.lang.Object"));

        assertThat(context.containsBean(TestManager.RUNTIME_CONTEXT_TESTBEAN_NAME), is(true));
        Object bean = context.getBean(TestManager.RUNTIME_CONTEXT_TESTBEAN_NAME);
        assertThat(bean, is(sameInstance(testInstance)));

    }

    @Test
    public void testCreateRuntimeContextsDependencyInjection() {


        @TestConfig
        class FakeTest {
            @Autowired
            @Qualifier("foo")
            public String foo = "foo";
            @Resource(name = "bar")
            public String bar = "bar";
        }

        AnnotationConfigApplicationContext parent = new AnnotationConfigApplicationContext(ParentContext.class);
        FakeTest fakeTest = new FakeTest();

        RuntimeContextUtils.createRuntimeContext(fakeTest, parent, null);

        assertThat(fakeTest.foo, is("FOO"));
        assertThat(fakeTest.bar, is("BAR"));

    }

    @Configuration
    static class ParentContext {
        @Bean
        public String foo() {
            return "FOO";
        }

        @Bean
        public String bar() {
            return "BAR";
        }
    }

}
