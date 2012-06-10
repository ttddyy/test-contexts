package net.ttddyy.testcontexts.core.suport.testng;

import net.ttddyy.testcontexts.core.TestConfig;
import net.ttddyy.testcontexts.core.DefaultTestManager;
import org.springframework.core.annotation.AnnotationUtils;
import org.testng.*;

import java.util.Collection;

/**
 * @author Tadaya Tsuyukubo
 */
public class TestNGSupportListener extends TestListenerAdapter implements ISuiteListener {

    private DefaultTestManager testManager = new DefaultTestManager();

    public TestNGSupportListener() {
        System.out.println("CONSTRUCTOR");
    }

    // suite start
    public void onStart(ISuite suite) {
        // before @BeforeSuite

        System.out.println("suite start");

        //TODO: create test manager

    }

    public void onFinish(ISuite suite) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onStart(ITestContext testContext) {
        // after @BeforeSuite, before @BeforeClass

        // TODO: create test context
        // - get @TestConfig
        // - get test class
        // - ApplicationContext tc = testManager.createTestAppContext(testClass)
        // tc.fireEvent("start_before_class")



        // called when entire test suite started
        Collection<ITestClass> a = ((TestRunner) testContext).getTestClasses();
        System.out.println("testng support started");
        super.onStart(testContext);
    }

    @Override
    public void onTestStart(ITestResult result) {
        final Object testInstance = result.getInstance();
        final Class<?> testClass = result.getTestClass().getRealClass();
        final TestConfig testConfig = AnnotationUtils.findAnnotation(testClass, TestConfig.class);
        final String contextName = testConfig.context();


//        final ApplicationContext applicationContext = testManager.getContext(contextName);

        // auto wire from context
//        testManager.autoWire(contextName, testInstance);

        // resolve dependency

        // called per each test method
        System.out.println("Test Start");
        System.out.println(result.getTestClass().getRealClass());

        // TODO: if this class is first time, then call before class

        // TODO: call before method

        super.onTestStart(result);
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        onTestMethodEnd(tr);
        super.onTestSuccess(tr);
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        onTestMethodEnd(tr);
        super.onTestFailure(tr);
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        onTestMethodEnd(tr);
        super.onTestSkipped(tr);
    }

    private void onTestMethodEnd(ITestResult tr) {
        // TODO: call after method
    }
}
