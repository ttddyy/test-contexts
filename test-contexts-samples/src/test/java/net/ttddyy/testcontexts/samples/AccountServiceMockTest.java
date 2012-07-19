package net.ttddyy.testcontexts.samples;

import net.ttddyy.testcontexts.core.TestConfig;
import net.ttddyy.testcontexts.samples.domain.Account;
import net.ttddyy.testcontexts.samples.domain.AccountDao;
import net.ttddyy.testcontexts.samples.domain.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Tadaya Tsuyukubo
 */
@RunWith(MyJUnitRunner.class)
@TestConfig(context = "serviceWithMock")
public class AccountServiceMockTest {

    @Resource
    private AccountDao accountDao;

    @Autowired
    private AccountService service;

    @Test
    public void testDeposit() {
        Account account = new Account(1, "foo", 150);
        when(accountDao.getById(1)).thenReturn(account);

        long result = service.deposit(1, 5);
        assertThat(result, is(155L));

        verify(accountDao, only()).getById(1);
    }
}
