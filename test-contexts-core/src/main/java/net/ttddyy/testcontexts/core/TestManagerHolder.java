package net.ttddyy.testcontexts.core;

/**
 * @author Tadaya Tsuyukubo
 */
public class TestManagerHolder {

    // TODO: make strategy

    private static TestManager testManager;

    public static TestManager get() {
        return testManager;
    }

    public static void set(TestManager testManager) {
        TestManagerHolder.testManager = testManager;
    }
}
