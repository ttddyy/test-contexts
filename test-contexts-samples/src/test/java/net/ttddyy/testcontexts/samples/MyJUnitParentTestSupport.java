package net.ttddyy.testcontexts.samples;

import net.ttddyy.testcontexts.core.SpecifyContextDefinitionClasses;
import net.ttddyy.testcontexts.core.suport.junit4.AbstractJUnit4Support;

/**
 * @author Tadaya Tsuyukubo
 */
@SpecifyContextDefinitionClasses(classes = MyTestConfig.class)
public class MyJUnitParentTestSupport extends AbstractJUnit4Support {

}
