package net.ttddyy.testcontexts;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.springframework.util.ObjectUtils;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
public class TestNGUtils {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static void runAndVerify(Class<?>... testClasses) {

        if (ObjectUtils.isEmpty(testClasses)) {
            throw new AssertionError("no test class specified.");
        }

        TestListenerAdapter tla = new TestListenerAdapter();
        TestNG testNG = new TestNG();
        testNG.setTestClasses(testClasses);
        testNG.addListener(tla);
        testNG.run();

        List<ITestResult> failedTests = tla.getFailedTests();
        if (!failedTests.isEmpty()) {
            List<String> errorMessages = Lists.newArrayList();
            errorMessages.addAll(Lists.transform(failedTests, new Function<ITestResult, String>() {
                int i = 1;

                @Override
                public String apply(ITestResult testResult) {
                    String stackTraceString = Throwables.getStackTraceAsString(testResult.getThrowable());
                    String template = "Message-%d: %n %s";
                    return String.format(template, i++, stackTraceString);
                }
            }));

            String header = String.format("Combined Messages (Total:%d)", errorMessages.size());
            errorMessages.add(0, header);

            String message = Joiner.on(LINE_SEPARATOR).join(errorMessages);

            throw new AssertionError(message);
        }
    }
}
