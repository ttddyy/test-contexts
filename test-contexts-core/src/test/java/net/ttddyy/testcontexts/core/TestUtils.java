package net.ttddyy.testcontexts.core;

/**
 * Utility class for unit testing.
 *
 * @author Tadaya Tsuyukubo
 */
public class TestUtils {

    public static void clearContextManager() {
        TestManager contextManager = TestManagerHolder.get();
        if (contextManager != null) {
            contextManager.clear();
        }
    }

}
