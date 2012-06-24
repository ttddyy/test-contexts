package net.ttddyy.testcontexts.core.suport.testng;

import net.ttddyy.testcontexts.core.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Tadaya Tsuyukubo
 */
public class AbstractTestNGSupport implements ApplicationContextAware {

    // TODO: implement IHookable to trigger after exception thrown

    protected TestManager testManager;

    protected ApplicationContext applicationContext;

    static {
        final TestManagerBootStrap bootStrap = new TestManagerBootStrap();
        bootStrap.createRootContext();
    }

    public AbstractTestNGSupport() {

        // initialize configured contexts
        synchronized (AbstractTestNGSupport.class) {

            final SpecifyContextDefinitionClasses annotation =
                    AnnotationUtils.findAnnotation(this.getClass(), SpecifyContextDefinitionClasses.class);
            if (annotation == null) {
                throw new TestContextException("@SpecifyContextDefinitionClasses is not annotated.");
            }

            testManager = TestManagerHolder.get();
            testManager.prepareConfiguredContext(annotation.classes());
        }

        // create app context for the test instance
        testManager.createRuntimeContext(this);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @BeforeSuite(alwaysRun = true)
    protected void publishBeforeSuiteEvent() {
        publishEvent(null, TestLifecycleEventType.BEFORE_SUITE);
    }

    @BeforeClass(alwaysRun = true)
    protected void publishBeforeClassEvent() {
        publishEvent(null, TestLifecycleEventType.BEFORE_CLASS);
    }

    @BeforeMethod(alwaysRun = true)
    protected void publishBeforeMethodEvent(Method testMethod) {
        publishEvent(testMethod, TestLifecycleEventType.BEFORE_METHOD);
    }

    @AfterMethod(alwaysRun = true)
    protected void publishAfterMethodEvent(Method testMethod) {
        publishEvent(testMethod, TestLifecycleEventType.AFTER_METHOD);
    }

    @AfterClass(alwaysRun = true)
    protected void publishAfterClassEvent() {
        publishEvent(null, TestLifecycleEventType.AFTER_CLASS);
    }

    @AfterSuite(alwaysRun = true)
    protected void publishAfterSuiteEvent() {
        publishEvent(null, TestLifecycleEventType.AFTER_SUITE);
    }

    private void publishEvent(Method testMethod, TestLifecycleEventType eventType) {
        TestEventStatus eventStatus = new TestEventStatus();
        eventStatus.setTestClass(this.getClass());
        eventStatus.setTestMethod(testMethod);

        final TestLifecycleEvent event = new TestLifecycleEvent(applicationContext, eventType, eventStatus);
        applicationContext.publishEvent(event);

    }
}
