package net.ttddyy.testcontexts.core;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Utils for @ConfiguredContext annotation.
 *
 * @author Tadaya Tsuyukubo
 */
public class ConfiguredContextAnnotationUtils {

    public static List<Method> getAnnotatedMethods(Class<?>... configuredClasses) {
        final List<Method> result = new ArrayList<Method>();
        for (Class<?> configuredClass : configuredClasses) {
            result.addAll(getAnnotatedMethods(configuredClass));
        }
        return result;
    }

    private static List<Method> getAnnotatedMethods(Class<?> configuredClass) {

        final List<Method> annotatedMethods = new ArrayList<Method>();

        final Method[] methods = ReflectionUtils.getUniqueDeclaredMethods(configuredClass);
        for (Method method : methods) {
            final ConfiguredContext context = AnnotationUtils.findAnnotation(method, ConfiguredContext.class);
            if (context == null) {
                continue;
            }

            annotatedMethods.add(method);
        }

        return annotatedMethods;
    }

    public static ConfiguredContext getTestConfig(Method method) {
        return AnnotationUtils.findAnnotation(method, ConfiguredContext.class);
    }

}
