package net.ttddyy.testcontexts.samples;

import net.ttddyy.testcontexts.core.TestConfig;
import net.ttddyy.testcontexts.samples.domain.Account;
import net.ttddyy.testcontexts.samples.domain.AccountDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;


/**
 * @author Tadaya Tsuyukubo
 */
@RunWith(Parameterized.class)
@TestConfig(context = "dao")
public class AccountDaoGetByIdTest extends MyJUnitParentTestSupport {

    private int id;
    private String expectedName;

    public AccountDaoGetByIdTest(int id, String expectedName) {
        this.id = id;
        this.expectedName = expectedName;
    }

    @Resource
    private AccountDao accountDao;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // id, expected name
                {1, "foo"},
                {2, "bar"}
        });
    }


    @Test
    public void testGetById() {
        Account account = accountDao.getById(id);
        assertThat(account, is(notNullValue()));
        assertThat(account.getId(), is(id));
        assertThat(account.getName(), is(expectedName));
    }
}
