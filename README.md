# Test Contexts (Yet Another Spring Test Context Framework)

## About

Test Contexts is a framework that integrates spring-framework and test frameworks, junit and testng.

- each test class run inside of application contexts
- test will have a specified parent application context
- parent application contexts can have hierarchy
- simple annotation to specify parent application context in test
- annotation based central configuration file for application contexts


### Each test class run inside of application contexts
This allows tests to take advantage of ApplicationContext features, such as dependency injection, application context events, environment profiles, etc.

### test will have a specified parent application context
As stated above, each test runs inside of application context(runtime-context). This means the runtime-context can have a parent application context(configured context).
Configured context is specified by an annotation on test class. For example, dao layer tests can have a configured context that defines database related beans, and service layer beans may have a configured context that defines mock related beans. In test classes, those beans specified in configured contexts will be injected to  the test class instances.

### parent application contexts can have hierarchy

each context lifecycle can be different. For example, let's say you have a configured context foo and bar. foo is a parent of bar. After each test, you can refresh context bar because the test makes context-bar dirty.
you can refresh configured context foo

### simple annotation to specify parent application context in test
In each test class, you can simply specify which configured context to use as parent by specifying string name in @TestConfig annotation.

### annotation based central configuration file for application contexts
configured context are defined as a java class with annotation.
example:



## Difference from Spring TestContext Framework

**more visibility**

In Test Contexts, all framework related classes are exposed as beans in framework context.
You can inject/retrieve them, call methods, or override any beans easily.


**ApplicationContext hierarchy**

Test Contexts supports parent application contexts.
As of spring 3.1, hierarchical application context are not yet supported in spring-tcf.


**All tests run inside of application contexts**

This allows tests to take advantage of ApplicationContext features, such as dependency injection, application context events, environment profiles, etc.


**Single place to look at test application contexts definitions**

Similar to JavaConfig, application contexts used by test(Configured Context) are defined in a java class.
So, it is easy to look up the configuration.


# Sample Tests


````java

    // Configured Context(ApplicationContext) definition
    @ConfiguredContextDefinition
    public class MyTestContexts {

        // use annotation class to create application context
        @ConfiguredContext(
            name = "contextFoo",
            classes = ConfigFoo.class
        )
        public void configFoo() {
        }

        // use xml file to create application context

        @ConfiguredContext(
            name = "contextBar",
            locations = "bar-context.xml",
            parent = "contextFoo"  // child of contextFoo
        )
        public void configBar() {
        }
    }
````

````java
    // (optional) abstract class for all test classes
    public abstract class TestBase extends AbstractTestNGSupport {

        static {
            // specify test contexts class
            configClasses.add(MyTestContexts.class);
        }
        ....
    }
````

````java
    @TestConfig(context = "contextFoo")
    public class FooTest extends TestBase {
        ....
    }

    @TestConfig(context = "contextBar")
    public class FooTest extends TestBase {
        ....
    }
````

# Architecture

[Application Contexts](https://docs.google.com/drawings/d/1kLSdwxMUdfcYO4qqBYGAZTukQ6sGjmySGihZNnHAuqE/edit)

## Framework Context
Framework creates this context. This is the root context of all other application context.
The context is created at the startup time and defines framework related beans, such as TestManager bean.


## Configured Context

User defined application contexts.
User can define multiple application contexts for test with parent-child hierarchy. Configured Contexts are specified as java methods in the class with @TestContextDefinition annotation.


## Runtime Context

At runtime, each test instance(junit or testng instance) will run inside of own application context having a configured context as its parent application context.


## Test Lifecycle Listener

Test Contexts framework calls back lifecycle event which is implemented as a spring's ApplicationContextEvent.


# TestNG Integration

## Use Inheritance

AbstractTestNGSupport class

## Use Listener

...

# JUnit4 Integration

...
