package net.ttddyy.testcontexts.core;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * @author Tadaya Tsuyukubo
 */
public class ConfiguredContextDefinitionValidatorTests {

    private ConfiguredContextDefinitionValidator validator = new ConfiguredContextDefinitionValidator();

    @Test(expected = TestContextException.class)
    public void testNoConfigClass() {
        validator.validate(null);
    }

    @Test(expected = TestContextException.class)
    public void testNonAnnotatedConfigClass() {

        // not @ConfiguredContextDefinition annotated
        class NotAnnotated {

            @ConfiguredContext
            public void foo() {
            }
        }
        validator.validate(NotAnnotated.class);
    }

    @Test
    public void testNoAnnotatedMethods() {

        @ConfiguredContextDefinition
        class NotAnnotated {
            public void foo() {
            }
        }
        validator.validate(NotAnnotated.class);
        // should run fine
    }

    @Test(expected = TestContextException.class)
    public void testMethodReturnAppCtxButHaveXmlConfigs() {

        @ConfiguredContextDefinition
        class Configuration {
            @ConfiguredContext(
                    locations = "foo.xml"
            )
            public ApplicationContext foo() {
                return null;
            }
        }

        validator.validate(Configuration.class);
    }

    @Test(expected = TestContextException.class)
    public void testMethodReturnAppCtxButHaveJavaConfigs() {

        @Configuration
        class JavaConfig {
        }

        @ConfiguredContextDefinition
        class Configuration {
            @ConfiguredContext(
                    classes = JavaConfig.class
            )
            public ApplicationContext foo() {
                return null;
            }
        }

        validator.validate(Configuration.class);
    }

    @Test(expected = TestContextException.class)
    public void testBothXmlAndJavaConfigSpecified() {

        @Configuration
        class JavaConfig {
        }

        @ConfiguredContextDefinition
        class Configuration {
            @ConfiguredContext(
                    locations = "foo.xml",
                    classes = JavaConfig.class
            )
            public void foo() {
            }
        }

        validator.validate(Configuration.class);
    }

    @Test(expected = TestContextException.class)
    public void testNeitherXmlNorJavaConfigSpecified() {

        @ConfiguredContextDefinition
        class Configuration {
            @ConfiguredContext
            public void foo() {
            }
        }

        validator.validate(Configuration.class);
    }

    @Test(expected = TestContextException.class)
    public void testSpecifiedParentContextNotExist() {

        @ConfiguredContextDefinition
        class Configuration {
            @ConfiguredContext(parent = "not-exist")
            public void foo() {
            }
        }

        validator.validate(Configuration.class);
    }

    @Test(expected = TestContextException.class)
    public void testSpecifyOwnContextAsParent() {

        @ConfiguredContextDefinition
        class Configuration {
            @ConfiguredContext(name = "foo", parent = "foo")
            public void foo() {
            }
        }

        validator.validate(Configuration.class);
    }

    @Test(expected = TestContextException.class)
    public void testCircularReference() {

        @ConfiguredContextDefinition
        class Configuration {
            @ConfiguredContext(name = "valid")
            public void valid() {
            }

            @ConfiguredContext(name = "foo", parent = "baz")
            public void foo() {
            }

            @ConfiguredContext(name = "bar", parent = "foo")
            public void bar() {
            }

            @ConfiguredContext(name = "baz", parent = "bar")
            public void baz() {
            }
        }

        validator.validate(Configuration.class);
    }

}
