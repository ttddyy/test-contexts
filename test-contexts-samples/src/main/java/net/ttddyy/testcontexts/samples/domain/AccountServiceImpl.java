package net.ttddyy.testcontexts.samples.domain;

import javax.annotation.Resource;

/**
 * @author Tadaya Tsuyukubo
 */
public class AccountServiceImpl implements AccountService {

    @Resource
    private AccountDao accountDao;

    public long deposit(int accountId, long amount) {
        Account account = accountDao.getById(accountId);
        long newAmount = account.getAmount() + amount;
        account.setAmount(newAmount);
        return newAmount;
    }

    public long withdraw(int accountId, long amount) {
        Account account = accountDao.getById(accountId);

        long newAmount = account.getAmount() - amount;
        account.setAmount(newAmount);

        return newAmount;
    }
}
