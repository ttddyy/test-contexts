package net.ttddyy.testcontexts.core;

/**
 * @author Tadaya Tsuyukubo
 */
public class RuntimeContextMetaInfo extends TestContextMetaInfo {

    public static enum TestType {
        JUNIT4, TESTNG
    }

    private TestType testType;

    public RuntimeContextMetaInfo() {
        this.setContextType(ContextType.RUNTIME);
    }

    public TestType getTestType() {
        return testType;
    }

    public void setTestType(TestType testType) {
        this.testType = testType;
    }

}
