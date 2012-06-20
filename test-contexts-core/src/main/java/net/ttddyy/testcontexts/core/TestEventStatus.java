package net.ttddyy.testcontexts.core;

import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

/**
 * @author Tadaya Tsuyukubo
 */
public class TestEventStatus {

    private Class<?> testClass;
    private Method testMethod;


    public TestEventStatus() {
    }

    public TestEventStatus(Class<?> testClass, Method testMethod) {
        this.testClass = testClass;
        this.testMethod = testMethod;
    }

    public Class<?> getTestClass() {
        return testClass;
    }

    public void setTestClass(Class<?> testClass) {
        this.testClass = testClass;
    }

    public Method getTestMethod() {
        return testMethod;
    }

    public void setTestMethod(Method testMethod) {
        this.testMethod = testMethod;
    }

}
