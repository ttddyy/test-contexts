package net.ttddyy.testcontexts.core;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
public class ConfiguredContextDefinitionParserTests {

    private ConfiguredContextDefinitionParser parser = new ConfiguredContextDefinitionParser();


    @Test
    public void testFileBasedConfig() {

        List<ParsedConfiguredContextDefinition> definitions = parser.parse(FileBasedConfiguration.class);

        assertThat(definitions, hasSize(1));
        ParsedConfiguredContextDefinition definition = definitions.get(0);
        assertThat(definition.getContextName(), is("context-name"));
        assertThat(definition.getParentContextName(), is("parent-name"));
        assertThat(definition.getProfiles(), arrayWithSize(2));
        assertThat(definition.getProfiles(), arrayContainingInAnyOrder("profile1", "profile2"));
        assertThat(definition.getContextCreationStrategy(), is(ParsedConfiguredContextDefinition.ContextCreationStrategy.BY_CONFIG_FILE));
        assertThat(definition.getDefinitionFiles(), arrayWithSize(2));
        assertThat(definition.getDefinitionFiles(), arrayContainingInAnyOrder("location1", "location2"));
        assertThat(definition.getDefinitionClasses(), emptyArray());
        assertThat(definition.getDefinitionMethod(), nullValue());
        assertThat(definition.getListeners(), arrayWithSize(2));
        assertThat(definition.getListeners(), arrayContainingInAnyOrder(
                (Class) DummyListenerFoo.class, (Class) DummyListenerBar.class));

    }

    @Test
    public void testAnnotationBasedConfig() {

        List<ParsedConfiguredContextDefinition> definitions = parser.parse(AnnotationBasedConfiguration.class);

        assertThat(definitions, hasSize(1));
        ParsedConfiguredContextDefinition definition = definitions.get(0);
        assertThat(definition.getContextName(), is("context-name"));
        assertThat(definition.getParentContextName(), is("parent-name"));
        assertThat(definition.getProfiles(), arrayWithSize(2));
        assertThat(definition.getProfiles(), arrayContainingInAnyOrder("profile1", "profile2"));
        assertThat(definition.getContextCreationStrategy(), is(ParsedConfiguredContextDefinition.ContextCreationStrategy.BY_ANNOTATED_CLASS));
        assertThat(definition.getDefinitionClasses(), arrayWithSize(2));
        assertThat(definition.getDefinitionClasses(), arrayContainingInAnyOrder(AnnotationBasedConfiguration.FooJavaConfig.class, AnnotationBasedConfiguration.BarJavaConfig.class));
        assertThat(definition.getDefinitionFiles(), emptyArray());
        assertThat(definition.getDefinitionMethod(), nullValue());
        assertThat(definition.getListeners(), arrayWithSize(2));
        assertThat(definition.getListeners(), arrayContainingInAnyOrder(
                (Class) DummyListenerFoo.class, (Class) DummyListenerBar.class));

    }

    @Test
    public void testMethodBasedConfig() {

        List<ParsedConfiguredContextDefinition> definitions = parser.parse(MethodBasedConfiguration.class);

        assertThat(definitions, hasSize(1));
        ParsedConfiguredContextDefinition definition = definitions.get(0);
        assertThat(definition.getContextName(), is("context-name"));
        assertThat(definition.getParentContextName(), is("parent-name"));
        assertThat(definition.getProfiles(), arrayWithSize(2));
        assertThat(definition.getProfiles(), arrayContainingInAnyOrder("profile1", "profile2"));
        assertThat(definition.getContextCreationStrategy(), is(ParsedConfiguredContextDefinition.ContextCreationStrategy.BY_METHOD_INVOCATION));
        assertThat(definition.getDefinitionMethod(), notNullValue());
        assertThat(definition.getDefinitionMethod().getName(), is("foo"));
        assertThat(definition.getDefinitionFiles(), emptyArray());
        assertThat(definition.getDefinitionClasses(), emptyArray());
        assertThat(definition.getListeners(), arrayWithSize(2));
        assertThat(definition.getListeners(), arrayContainingInAnyOrder(
                (Class) DummyListenerFoo.class, (Class) DummyListenerBar.class));
    }

    @Test
    public void testWithMultipleConfigurationClases() {
        List<ParsedConfiguredContextDefinition> definitions = parser.parse(
                FileBasedConfiguration.class, AnnotationBasedConfiguration.class, MethodBasedConfiguration.class);

        assertThat(definitions, hasSize(3));
    }

    @Test
    public void testNoTestContextConfig() {

        // no method with @ConfiguredContext
        @ConfiguredContextDefinition
        class NoTestContextConfigConfiguration {
            public void foo() {
            }
        }

        List<ParsedConfiguredContextDefinition> definitions = parser.parse(NoTestContextConfigConfiguration.class);
        assertThat(definitions, hasSize(0));
    }

    @Test
    public void testContextNameOverride() {

        // when name is not specified, method name will be used as a context name
        @ConfiguredContextDefinition
        class ContextNameOverrideConfiguration {
            @ConfiguredContext
            public void foo() {
            }
        }

        List<ParsedConfiguredContextDefinition> definitions = parser.parse(ContextNameOverrideConfiguration.class);

        assertThat(definitions, hasSize(1));
        ParsedConfiguredContextDefinition definition = definitions.get(0);
        assertThat(definition.getContextName(), is("foo"));
    }

    @Test
    public void testEmptyParentContext() {

        // when name is not specified, method name will be used as a context name
        @ConfiguredContextDefinition
        class ParentContextNotSpecifiedConfiguration {
            @ConfiguredContext(name = "context-name")
            public void foo() {
            }
        }

        @ConfiguredContextDefinition
        class ExplicitlyParentContextNotSpecifiedConfiguration {
            @ConfiguredContext(name = "context-name", parent = "")
            public void foo() {
            }
        }

        List<ParsedConfiguredContextDefinition> definitions = parser.parse(ParentContextNotSpecifiedConfiguration.class);
        assertThat(definitions, hasSize(1));
        assertThat(definitions.get(0).getParentContextName(), nullValue());

        definitions = parser.parse(ExplicitlyParentContextNotSpecifiedConfiguration.class);
        assertThat(definitions, hasSize(1));
        assertThat(definitions.get(0).getParentContextName(), nullValue());

    }

    @Test
    public void testDefinitionsOrderSimple() {

        // when name is not specified, method name will be used as a context name
        @ConfiguredContextDefinition
        class SimpleConfiguration {

            @ConfiguredContext(parent = "second")
            public void third() {
            }

            @ConfiguredContext
            public void first() {
                // no parent should be first
            }

            @ConfiguredContext(parent = "first")
            public void second() {
            }

        }

        List<ParsedConfiguredContextDefinition> definitions = parser.parse(SimpleConfiguration.class);
        assertThat(definitions, hasSize(3));
        assertThat(definitions.get(0).getContextName(), is("first"));
        assertThat(definitions.get(1).getContextName(), is("second"));
        assertThat(definitions.get(2).getContextName(), is("third"));
        assertThat(definitions.get(0).getOrder(), is(1));
        assertThat(definitions.get(1).getOrder(), is(2));
        assertThat(definitions.get(2).getOrder(), is(3));
    }

    @Test
    public void testDefinitionsOrderComplex() {

        @ConfiguredContextDefinition
        class ComplexConfiguration {

            // fooA -- barA -- bazA
            //              -- bazB
            //      -- barB -- bazC
            //      -- barC
            // fooB -- barD -- bazD
            // fooC

            @ConfiguredContext
            public void fooB() {
            }

            @ConfiguredContext(parent = "fooA")
            public void barC() {
            }

            @ConfiguredContext(parent = "fooA")
            public void barB() {
            }

            @ConfiguredContext(parent = "barA")
            public void bazB() {
            }

            @ConfiguredContext(parent = "barD")
            public void bazD() {
            }

            @ConfiguredContext(parent = "barB")
            public void bazC() {
            }

            @ConfiguredContext
            public void fooA() {
            }

            @ConfiguredContext(parent = "fooA")
            public void barA() {
            }

            @ConfiguredContext(parent = "fooB")
            public void barD() {
            }

            @ConfiguredContext
            public void fooC() {
            }

            @ConfiguredContext(parent = "barA")
            public void bazA() {
            }

        }

        Function<ParsedConfiguredContextDefinition, String> toContextName = new Function<ParsedConfiguredContextDefinition, String>() {
            @Override
            public String apply(ParsedConfiguredContextDefinition input) {
                return input.getContextName();
            }
        };

        List<ParsedConfiguredContextDefinition> definitions = parser.parse(ComplexConfiguration.class);
        assertThat(definitions, hasSize(11));

        List<String> sortedContextNames = Lists.transform(definitions, toContextName);
        int indexFooA = sortedContextNames.indexOf("fooA");
        int indexFooB = sortedContextNames.indexOf("fooB");
        int indexFooC = sortedContextNames.indexOf("fooC");
        int indexBarA = sortedContextNames.indexOf("barA");
        int indexBarB = sortedContextNames.indexOf("barB");
        int indexBarC = sortedContextNames.indexOf("barC");
        int indexBarD = sortedContextNames.indexOf("barD");
        int indexBazA = sortedContextNames.indexOf("bazA");
        int indexBazB = sortedContextNames.indexOf("bazB");
        int indexBazC = sortedContextNames.indexOf("bazC");
        int indexBazD = sortedContextNames.indexOf("bazD");

        assertThat(indexFooA, is(lessThan(indexBarA)));
        assertThat(indexFooA, is(lessThan(indexBarB)));
        assertThat(indexFooA, is(lessThan(indexBarC)));
        assertThat(indexFooA, is(lessThan(indexBazA)));
        assertThat(indexFooA, is(lessThan(indexBazB)));
        assertThat(indexFooA, is(lessThan(indexBazC)));

        assertThat(indexFooB, is(lessThan(indexBarD)));
        assertThat(indexFooB, is(lessThan(indexBazD)));

        assertThat(indexBarA, is(lessThan(indexBazA)));
        assertThat(indexBarA, is(lessThan(indexBazB)));

        assertThat(indexBarB, is(lessThan(indexBazC)));

        assertThat(indexBarD, is(lessThan(indexBazD)));

    }

    @Test
    public void testDefinitionsChildContextNames() {
        // when name is not specified, method name will be used as a context name
        @ConfiguredContextDefinition
        class Configuration {

            // a => b,c
            // b => d
            // e

            @ConfiguredContext
            public void a() {
            }

            @ConfiguredContext(parent = "a")
            public void b() {
            }

            @ConfiguredContext(parent = "a")
            public void c() {
            }

            @ConfiguredContext(parent = "b")
            public void d() {
            }

            @ConfiguredContext
            public void e() {
            }

        }

        List<ParsedConfiguredContextDefinition> definitions = parser.parse(Configuration.class);
        assertThat(definitions, hasSize(5));

        Map<String, ParsedConfiguredContextDefinition> definitionByContextName = Maps.uniqueIndex(definitions,
                new Function<ParsedConfiguredContextDefinition, String>() {
                    @Override
                    public String apply(ParsedConfiguredContextDefinition definition) {
                        return definition.getContextName();
                    }
                }
        );

        assertThat(definitionByContextName.get("a").getChildContextNames(), arrayContainingInAnyOrder("b", "c"));
        assertThat(definitionByContextName.get("b").getChildContextNames(), arrayContainingInAnyOrder("d"));
        assertThat(definitionByContextName.get("c").getChildContextNames(), emptyArray());
        assertThat(definitionByContextName.get("d").getChildContextNames(), emptyArray());
        assertThat(definitionByContextName.get("e").getChildContextNames(), emptyArray());

    }

    @ConfiguredContextDefinition
    public static class FileBasedConfiguration {

        @ConfiguredContext(
                name = "context-name",
                parent = "parent-name",
                profiles = {"profile1", "profile2"},
                locations = {"location1", "location2"},
                listeners = {DummyListenerFoo.class, DummyListenerBar.class}
        )
        public void foo() {
        }
    }

    @ConfiguredContextDefinition
    public static class AnnotationBasedConfiguration {

        @ConfiguredContext(
                name = "context-name",
                parent = "parent-name",
                profiles = {"profile1", "profile2"},
                classes = {FooJavaConfig.class, BarJavaConfig.class},
                listeners = {DummyListenerFoo.class, DummyListenerBar.class}
        )
        public void foo() {
        }

        @Configuration
        public static class FooJavaConfig {
        }

        @Configuration
        public static class BarJavaConfig {
        }
    }

    @ConfiguredContextDefinition
    public static class MethodBasedConfiguration {

        @ConfiguredContext(
                name = "context-name",
                parent = "parent-name",
                profiles = {"profile1", "profile2"},
                locations = {"location1", "location2"},
                listeners = {DummyListenerFoo.class, DummyListenerBar.class}
        )
        public ApplicationContext foo(ApplicationContext parent) {
            return new GenericApplicationContext(parent);
        }
    }

    public static class DummyListenerFoo extends TestLifecycleEventListenerAdapter {
    }

    public static class DummyListenerBar extends TestLifecycleEventListenerAdapter {
    }

}
