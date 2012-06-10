package net.ttddyy.testcontexts.core;

/**
 * Runtime exception thrown by framework.
 *
 * @author Tadaya Tsuyukubo
 */
public class TestContextException extends RuntimeException {

    public TestContextException() {
    }

    public TestContextException(String s) {
        super(s);
    }

    public TestContextException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public TestContextException(Throwable throwable) {
        super(throwable);
    }
}
