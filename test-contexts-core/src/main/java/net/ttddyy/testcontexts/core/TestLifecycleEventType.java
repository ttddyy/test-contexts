package net.ttddyy.testcontexts.core;

/**
 * @author Tadaya Tsuyukubo
 */
public enum TestLifecycleEventType {

    BEFORE_ALL_TESTS,
    AFTER_ALL_TESTS,

    BEFORE_SUITE,
    AFTER_SUITE,

    BEFORE_CLASS,
    AFTER_CLASS,

    BEFORE_METHOD,
    AFTER_METHOD,
}
