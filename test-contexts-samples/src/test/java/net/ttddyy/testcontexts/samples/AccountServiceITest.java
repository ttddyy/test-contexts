package net.ttddyy.testcontexts.samples;

import net.ttddyy.testcontexts.core.TestConfig;
import net.ttddyy.testcontexts.samples.domain.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
@RunWith(MyJUnitRunner.class)
@TestConfig(context = "serviceWithDao")
public class AccountServiceITest {

    @Autowired
    private AccountService service;

    @Test
    public void testDeposit() {
        long result = service.deposit(1, 100);
        assertThat(result, is(200L));
    }

    @Test
    public void testWithdraw() {
        long result = service.withdraw(2, 200);
        assertThat(result, is(0L));
    }
}
