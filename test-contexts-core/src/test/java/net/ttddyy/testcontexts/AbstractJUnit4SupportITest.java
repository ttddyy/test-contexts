package net.ttddyy.testcontexts;

import com.google.common.collect.Sets;
import net.ttddyy.testcontexts.core.suport.junit4.AbstractJUnit4Support;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Tadaya Tsuyukubo
 */
public class AbstractJUnit4SupportITest extends AbstractJUnit4Support {
    static {
//        AbstractJUnit4Support.classes.add(String.class);
        System.out.println("TEST class static block");
    }

//    @Override
    protected Set<Class<?>> getClasses() {
        Set<Class<?>> classes = Sets.newHashSet();
        classes.add(String.class);
        return classes;
    }

    @Test
    public void test() {
        assertThat(true, is(true));
        assertThat(true, is(false));
    }
}
