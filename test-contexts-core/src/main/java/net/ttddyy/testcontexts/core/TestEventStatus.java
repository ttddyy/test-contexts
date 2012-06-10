package net.ttddyy.testcontexts.core;

import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

/**
 * @author Tadaya Tsuyukubo
 */
public class TestEventStatus {

    private Object testInstance;
    private Method testMethod;

    public Object getTestInstance() {
        return testInstance;
    }

    public void setTestInstance(Object testInstance) {
        this.testInstance = testInstance;
    }

    public Method getTestMethod() {
        return testMethod;
    }

    public void setTestMethod(Method testMethod) {
        this.testMethod = testMethod;
    }

}
