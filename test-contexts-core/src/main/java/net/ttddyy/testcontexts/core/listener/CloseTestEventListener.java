package net.ttddyy.testcontexts.core.listener;

import net.ttddyy.testcontexts.core.TestLifecycleEvent;
import net.ttddyy.testcontexts.core.TestLifecycleEventType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Tadaya Tsuyukubo
 */
public class CloseTestEventListener implements ApplicationListener<TestLifecycleEvent> {

    // TODO: close the app context
    // different from refresh(dirty) listener


    public void onApplicationEvent(TestLifecycleEvent event) {

        final TestLifecycleEventType eventType = event.getEventType();

        final ApplicationContext applicationContext = event.getApplicationContext();

        if (TestLifecycleEventType.AFTER_CLASS == eventType) {
            if (applicationContext instanceof ConfigurableApplicationContext) {
                ((ConfigurableApplicationContext) applicationContext).close();
            }
        }

    }
}
