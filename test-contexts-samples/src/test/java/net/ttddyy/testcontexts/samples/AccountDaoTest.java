package net.ttddyy.testcontexts.samples;

import net.ttddyy.testcontexts.core.TestConfig;
import net.ttddyy.testcontexts.samples.domain.Account;
import net.ttddyy.testcontexts.samples.domain.AccountDao;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;


/**
 * @author Tadaya Tsuyukubo
 */
@RunWith(MyJUnitRunner.class)
@TestConfig(context = "dao")
public class AccountDaoTest {

    @Resource
    private AccountDao accountDao;

    @Test
    public void testGetById() {
        Account account = accountDao.getById(1);
        assertThat(account, is(notNullValue()));
        assertThat(account.getId(), is(1));
        assertThat(account.getName(), is("foo"));
    }
}
