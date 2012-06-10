package net.ttddyy.testcontexts.core;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * TestContexts framework event class.
 *
 * Implemented as a spring ApplicationContextEvent since tests run in ApplicationContext.
 *
 *
 * @author Tadaya Tsuyukubo
 */
public class TestLifecycleEvent extends ApplicationContextEvent {

    private TestLifecycleEventType eventType;
    private TestEventStatus eventStatus;

    public TestLifecycleEvent(ApplicationContext source, TestLifecycleEventType eventType, TestEventStatus eventStatus) {
        super(source);
        this.eventType = eventType;
        this.eventStatus = eventStatus;
    }


    public TestLifecycleEventType getEventType() {
        return eventType;
    }

    public TestEventStatus getEventStatus() {
        return eventStatus;
    }
}
