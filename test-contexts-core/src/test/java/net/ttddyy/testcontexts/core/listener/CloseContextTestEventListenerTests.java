package net.ttddyy.testcontexts.core.listener;

import net.ttddyy.testcontexts.core.TestEventStatus;
import net.ttddyy.testcontexts.core.TestLifecycleEvent;
import net.ttddyy.testcontexts.core.TestLifecycleEventType;
import net.ttddyy.testcontexts.core.TestManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CloseContextTestEventListener.
 *
 * @author Tadaya Tsuyukubo
 */
public class CloseContextTestEventListenerTests {

    @Test
    public void testAnnotationOnly() {
        CloseContextTestEventListener listener = new CloseContextTestEventListener();

        @CloseContext
        class DummyTest {
        }

        StaticApplicationContext context = new StaticApplicationContext();
        context.refresh();
        assertThat("make sure context is active at first", context.isActive(), is(true));

        TestEventStatus status = new TestEventStatus();
        status.setTestInstance(new DummyTest()); // dummy test instance
        TestLifecycleEvent event = new TestLifecycleEvent(context, TestLifecycleEventType.AFTER_CLASS, status);
        listener.onAfterClass(event);

        ApplicationContext applicationContext = event.getApplicationContext();

        assertThat(applicationContext, sameInstance((ApplicationContext) context));
        assertThat(context.isActive(), is(false));
    }

    @Test
    public void testWithContextNames() {

        CloseContextTestEventListener listener = new CloseContextTestEventListener();

        @CloseContext(contexts = {"foo", "bar"})
        class DummyTestWithContextNames {
        }

        // test manager
        TestManager testManager = mock(TestManager.class);
        ConfigurableApplicationContext fooContext = mock(ConfigurableApplicationContext.class);
        when(testManager.getConfiguredContext("foo")).thenReturn(fooContext);
        ConfigurableApplicationContext barContext = mock(ConfigurableApplicationContext.class);
        when(testManager.getConfiguredContext("bar")).thenReturn(barContext);
        listener.setTestManager(testManager);

        // context
        StaticApplicationContext context = new StaticApplicationContext();
        context.refresh();
        assertThat("make sure context is active at first", context.isActive(), is(true));

        // event
        TestEventStatus status = new TestEventStatus();
        status.setTestInstance(new DummyTestWithContextNames()); // dummy test instance
        TestLifecycleEvent event = new TestLifecycleEvent(context, TestLifecycleEventType.AFTER_CLASS, status);

        // invocation
        listener.onAfterClass(event);

        ApplicationContext applicationContext = event.getApplicationContext();
        assertThat(applicationContext, sameInstance((ApplicationContext) context));

        verify(fooContext).close();
        verify(barContext).close();
    }

}
