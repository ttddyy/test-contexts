# Test Contexts (Yet Another Spring Test Context Framework)

## About

Test Contexts is a framework that integrates spring-framework and test frameworks(junit and testng).

- each test class runs inside of application contexts
- each test class can specify a parent application context
- parent application contexts are hierarchical
- simple annotation to specify parent application context in test
- annotation based central configuration classes for application contexts


### Each test class runs inside of application contexts
This allows tests to take advantage of ApplicationContext features, such as dependency injection, application context events, environment profiles, etc.

### Each test class can specify a parent application context
As stated above, each test runs inside of application context(runtime-context). This means the runtime-context can have a parent application context(configured-context).
Configured-context is specified by an annotation(@TestConfig) on the test class.

For example, dao layer tests can have a configured context that defines database related beans.
Service layer beans may have a configured context that defines mock related beans.
At runtime, test instance runs inside of a runtime-context which has a specified parent configured-context.
So that all the beans defined in configured-context can be injected to the test instance.

### Parent application contexts are hierarchical
Configured-contexts can be hierarchical. This allows child context to override parent context defined beans.


### Simple annotation to specify parent application context in test
In each test class, @TestConfig annotation specifies which context(configured-context) to use as a prent context.


### Annotation based central configuration classes for application contexts
Similar to Spring JavaConfig, @ConfiguredContextDefinition annotated class is a central place to define all configured-contexts.
@ConfiguredContextDefinition annotated class contains @ConfiguredContext annotated methods. Each method provides meta info for configured-contexts.


example:

````java
    @ConfiguredContextDefinition
    public class ConfiguredContexts {

        @ConfiguredContext(name = "foo", locations = "applicationContext.xml")
        public void contextFoo() {  // xml based appCtx
        }
        @ConfiguredContext(name = "bar", classes = AnnotationAppContext.class)
        public void contextBar() {  // annotation based appCtx
        }
        @ConfiguredContext(name = "baz")
        public ApplicationContext contextBaz() {
            return new MyApplicationContext();  // custom appCtx
        }
    }
````


## Difference from Spring TestContext Framework

**more visibility**

In Test Contexts, all framework related classes are exposed as beans in framework-context.
You can inject/retrieve them, call methods, or override any beans easily.


**ApplicationContext hierarchy**

Test Contexts supports parent application contexts.

As of spring 3.1, hierarchical application context are not yet supported in spring-tcf.


**All tests run inside of application contexts**

This allows tests to take advantage of ApplicationContext features, such as dependency injection, application context events, environment profiles, etc.


**Single place to look at test application contexts definitions**

Similar to JavaConfig, application contexts used by test(configured-context) are defined in a java class(@ConfiguredContextDefinition).
So, it is easy to look up the configuration.


# Sample Tests


````java

    @ConfiguredContextDefinition   //  Configured-Context(ApplicationContext) definition
    public class MyTestContexts {

        @ConfiguredContext(        // use annotation class to create an application context
            name = "contextFoo",
            classes = ConfigFoo.class
        )
        public void configFoo() {
        }

        @ConfiguredContext(        // use xml file to create application context
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
            configClasses.add(MyTestContexts.class);  // specify test contexts class
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

![Application Contexts](https://docs.google.com/drawings/d/1kLSdwxMUdfcYO4qqBYGAZTukQ6sGjmySGihZNnHAuqE/edit)

## Framework Context

Test Contexts creates this context.

This is the root context of all other application contexts.
The framework-context is created at the startup time and defines framework related beans, such as TestManager bean, basic listeners, etc.


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
