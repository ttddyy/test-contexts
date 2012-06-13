package net.ttddyy.testcontexts.core.listener;

import net.ttddyy.testcontexts.core.TestEventStatus;
import net.ttddyy.testcontexts.core.TestLifecycleEvent;
import net.ttddyy.testcontexts.core.TestLifecycleEventType;
import net.ttddyy.testcontexts.core.TestManager;
import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ReflectionUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.*;

/**
 * @author Tadaya Tsuyukubo
 */
public class RefreshContextTestEventListenerTests {

    @Test
    public void testOnAfterClass() {

        @RefreshContext
        class DummyTest {
        }

        Object dummyTestInstance = new Object();

        // prepare test manager
        TestManager testManager = mock(TestManager.class);
        ConfigurableApplicationContext mockRuntimeAppCtx = mock(ConfigurableApplicationContext.class);
        when(testManager.createRuntimeContext(dummyTestInstance)).thenReturn(mockRuntimeAppCtx);

        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);
        when(context.getAutowireCapableBeanFactory()).thenReturn(beanFactory);
        when(beanFactory.getSingleton(TestManager.RUNTIME_CONTEXT_TESTBEAN_NAME)).thenReturn(dummyTestInstance);

        TestEventStatus status = new TestEventStatus();
        status.setTestInstance(new DummyTest()); // dummy test instance
        TestLifecycleEvent event = new TestLifecycleEvent(context, TestLifecycleEventType.AFTER_CLASS, status);

        RefreshContextTestEventListener listener = new RefreshContextTestEventListener();
        listener.setTestManager(testManager);

        // invocation
        listener.onAfterClass(event);

        ApplicationContext applicationContext = event.getApplicationContext();
        assertThat(applicationContext, sameInstance((ApplicationContext) context));

        // verify refresh logic for runtime-context is called
        verify(context).close();
        verify(testManager).createRuntimeContext(dummyTestInstance);
    }

    @Test
    public void testOnAfterClassWithContext() {

        @RefreshContext(contexts = {"foo", "bar"})
        class DummyTest {
        }

        Object dummyTestInstance = new Object();

        // prepare test manager
        TestManager testManager = mock(TestManager.class);
        ConfigurableApplicationContext mockRuntimeAppCtx = mock(ConfigurableApplicationContext.class);
        when(testManager.createRuntimeContext(dummyTestInstance)).thenReturn(mockRuntimeAppCtx);

        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);
        when(context.getAutowireCapableBeanFactory()).thenReturn(beanFactory);
        when(beanFactory.getSingleton(TestManager.RUNTIME_CONTEXT_TESTBEAN_NAME)).thenReturn(dummyTestInstance);

        TestEventStatus status = new TestEventStatus();
        status.setTestInstance(new DummyTest()); // dummy test instance
        TestLifecycleEvent event = new TestLifecycleEvent(context, TestLifecycleEventType.AFTER_CLASS, status);

        RefreshContextTestEventListener listener = new RefreshContextTestEventListener();
        listener.setTestManager(testManager);

        // invocation
        listener.onAfterClass(event);

        ApplicationContext applicationContext = event.getApplicationContext();
        assertThat(applicationContext, sameInstance((ApplicationContext) context));

        // verify test manager call for refreshing configured-contexts
        verify(testManager).refreshConfiguredContexts("foo", "bar");

        // verify refresh logic for runtime-context is called
        verify(context).close();
        verify(testManager).createRuntimeContext(dummyTestInstance);
    }

    @Test
    public void testOnAfterMethodWithMethodAnnotation() {

        class DummyTest {
            // method is annotated
            @RefreshContext
            void test() {
            }
        }

        Object dummyTestInstance = new Object();

        // test manager
        TestManager testManager = mock(TestManager.class);
        ConfigurableApplicationContext mockRuntimeAppCtx = mock(ConfigurableApplicationContext.class);
        when(testManager.createRuntimeContext(dummyTestInstance)).thenReturn(mockRuntimeAppCtx);

        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);
        when(context.getAutowireCapableBeanFactory()).thenReturn(beanFactory);
        when(beanFactory.getSingleton(TestManager.RUNTIME_CONTEXT_TESTBEAN_NAME)).thenReturn(dummyTestInstance);

        TestEventStatus status = new TestEventStatus();
        status.setTestInstance(new DummyTest()); // dummy test instance
        status.setTestMethod(ReflectionUtils.findMethod(DummyTest.class, "test"));
        TestLifecycleEvent event = new TestLifecycleEvent(context, TestLifecycleEventType.AFTER_METHOD, status);

        RefreshContextTestEventListener listener = new RefreshContextTestEventListener();
        listener.setTestManager(testManager);

        // invocation
        listener.onAfterMethod(event);

        ApplicationContext applicationContext = event.getApplicationContext();
        assertThat(applicationContext, sameInstance((ApplicationContext) context));

        // verify refresh logic for runtime-context is called
        verify(context).close();
        verify(testManager).createRuntimeContext(dummyTestInstance);
    }

    @Test
    public void testOnAfterMethodWithIgnoredClassMode() {

        class DummyTest {
            // annotated with invalid mode for method => will be ignored
            @RefreshContext(classMode = RefreshContext.ClassMode.AFTER_CLASS)
            void test() {
            }
        }

        Object dummyTestInstance = new Object();

        // test manager
        TestManager testManager = mock(TestManager.class);
        ConfigurableApplicationContext mockRuntimeAppCtx = mock(ConfigurableApplicationContext.class);
        when(testManager.createRuntimeContext(dummyTestInstance)).thenReturn(mockRuntimeAppCtx);

        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);
        when(context.getAutowireCapableBeanFactory()).thenReturn(beanFactory);
        when(beanFactory.getSingleton(TestManager.RUNTIME_CONTEXT_TESTBEAN_NAME)).thenReturn(dummyTestInstance);

        TestEventStatus status = new TestEventStatus();
        status.setTestInstance(new DummyTest()); // dummy test instance
        status.setTestMethod(ReflectionUtils.findMethod(DummyTest.class, "test"));
        TestLifecycleEvent event = new TestLifecycleEvent(context, TestLifecycleEventType.AFTER_METHOD, status);

        RefreshContextTestEventListener listener = new RefreshContextTestEventListener();
        listener.setTestManager(testManager);

        // invocation
        listener.onAfterMethod(event);

        ApplicationContext applicationContext = event.getApplicationContext();
        assertThat(applicationContext, sameInstance((ApplicationContext) context));

        // classmode=AFTER_CLASS will be ignored for method level annotation
        verify(context).close();
        verify(testManager).createRuntimeContext(dummyTestInstance);
    }

    @Test
    public void testOnAfterMethodWithClassAnnotation() {

        // class is annotated with mode
        @RefreshContext(classMode = RefreshContext.ClassMode.AFTER_EACH_TEST_METHOD)
        class DummyTest {
            void test() {
            }
        }

        Object dummyTestInstance = new Object();

        // test manager
        TestManager testManager = mock(TestManager.class);
        ConfigurableApplicationContext mockRuntimeAppCtx = mock(ConfigurableApplicationContext.class);
        when(testManager.createRuntimeContext(dummyTestInstance)).thenReturn(mockRuntimeAppCtx);

        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);
        when(context.getAutowireCapableBeanFactory()).thenReturn(beanFactory);
        when(beanFactory.getSingleton(TestManager.RUNTIME_CONTEXT_TESTBEAN_NAME)).thenReturn(dummyTestInstance);

        TestEventStatus status = new TestEventStatus();
        status.setTestInstance(new DummyTest()); // dummy test instance
        status.setTestMethod(ReflectionUtils.findMethod(DummyTest.class, "test"));
        TestLifecycleEvent event = new TestLifecycleEvent(context, TestLifecycleEventType.AFTER_METHOD, status);

        RefreshContextTestEventListener listener = new RefreshContextTestEventListener();
        listener.setTestManager(testManager);

        // invocation
        listener.onAfterMethod(event);

        ApplicationContext applicationContext = event.getApplicationContext();
        assertThat(applicationContext, sameInstance((ApplicationContext) context));

        // verify refresh logic for runtime-context is called
        verify(context).close();
        verify(testManager).createRuntimeContext(dummyTestInstance);
    }
}
