package net.ttddyy.testcontexts.core.listener;

import net.ttddyy.testcontexts.core.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.verification.VerificationMode;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CloseRuntimeContextTestEventListener}.
 *
 * @author Tadaya Tsuyukubo
 */
@RunWith(Parameterized.class)
public class CloseRuntimeContextTestEventListenerTests {

    private RuntimeContextMetaInfo.TestType testType;
    private boolean expectCloseAfterMethod;
    private boolean expectCloseAfterClass;


    public CloseRuntimeContextTestEventListenerTests(RuntimeContextMetaInfo.TestType testType, boolean expectCloseAfterMethod, boolean expectCloseAfterClass) {
        this.testType = testType;
        this.expectCloseAfterMethod = expectCloseAfterMethod;
        this.expectCloseAfterClass = expectCloseAfterClass;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // test type, expect close() is called after method, expect after class
                {RuntimeContextMetaInfo.TestType.JUNIT4, true, false},
                {RuntimeContextMetaInfo.TestType.TESTNG, false, true}
        });
    }

    @Test
    public void testOnAfterMethod() {

        CloseRuntimeContextTestEventListener listener = new CloseRuntimeContextTestEventListener();

        ApplicationContext context = getMockContext();

        TestEventStatus status = new TestEventStatus();
        TestLifecycleEvent event = new TestLifecycleEvent(context, TestLifecycleEventType.AFTER_METHOD, status);
        listener.onAfterMethod(event);

        // verify close() is called
        VerificationMode expected = expectCloseAfterMethod ? times(1) : never();
        verify((ConfigurableApplicationContext) context, expected).close();
    }

    @Test
    public void testOnAfterClass() {
        CloseRuntimeContextTestEventListener listener = new CloseRuntimeContextTestEventListener();

        ApplicationContext context = getMockContext();

        TestEventStatus status = new TestEventStatus();
        TestLifecycleEvent event = new TestLifecycleEvent(context, TestLifecycleEventType.AFTER_CLASS, status);
        listener.onAfterClass(event);

        // verify close() is called
        VerificationMode expected = expectCloseAfterClass ? times(1) : never();
        verify((ConfigurableApplicationContext) context, expected).close();
    }

    private ApplicationContext getMockContext() {
        // prepare runtime meta-info
        RuntimeContextMetaInfo metaInfo = new RuntimeContextMetaInfo();
        metaInfo.setTestType(testType);

        // register meta info bean
        ApplicationContext context = mock(ApplicationContext.class, withSettings().extraInterfaces(ConfigurableApplicationContext.class));
        ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);
        when(context.getAutowireCapableBeanFactory()).thenReturn(beanFactory);
        when(beanFactory.getSingleton(TestManager.METAINFO_BEAN_NAME)).thenReturn(metaInfo);

        return context;
    }

}
