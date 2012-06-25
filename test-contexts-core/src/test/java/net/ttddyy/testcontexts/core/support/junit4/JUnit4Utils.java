package net.ttddyy.testcontexts.core.support.junit4;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
public class JUnit4Utils {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static void runAndVerify(Class<?>... testClasses) {

        if (ObjectUtils.isEmpty(testClasses)) {
            throw new AssertionError("no test class specified.");
        }

        JUnitCore jUnitCore = new JUnitCore();

        Result result = jUnitCore.run(testClasses);

        if (!result.wasSuccessful()) {

            List<Failure> failures = result.getFailures();

            String header = String.format("Combined Messages (Total:%d)", failures.size());

            List<String> errorMessages = Lists.newArrayList();
            errorMessages.add(header);
            errorMessages.addAll(Lists.transform(failures, new Function<Failure, String>() {
                int i = 1;

                @Override
                public String apply(Failure failure) {
                    String stackTraceString = Throwables.getStackTraceAsString(failure.getException());
                    String template = "Message-%d: %n %s";
                    return String.format(template, i++, stackTraceString);
                }
            }));

            // transform messages to a single combined string
            String message = Joiner.on(LINE_SEPARATOR).join(errorMessages);
            throw new AssertionError(message);
        }

    }
}
