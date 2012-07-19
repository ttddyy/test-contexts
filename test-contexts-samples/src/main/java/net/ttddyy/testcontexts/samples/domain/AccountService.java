package net.ttddyy.testcontexts.samples.domain;

/**
 * @author Tadaya Tsuyukubo
 */
public interface AccountService {

    long deposit(int accountId, long amount);

    long withdraw(int accountId, long amount);

}
