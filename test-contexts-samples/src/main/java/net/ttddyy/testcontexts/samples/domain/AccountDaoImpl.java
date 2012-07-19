package net.ttddyy.testcontexts.samples.domain;

import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 */
public class AccountDaoImpl implements AccountDao, InitializingBean {

    private Map<Integer, Account> map = new HashMap<Integer, Account>();

    public void afterPropertiesSet() throws Exception {
        // put dummy data for now
        map.put(1, new Account(1, "foo", 100));
        map.put(2, new Account(2, "bar", 200));
        map.put(3, new Account(3, "baz", 300));

    }

    public Account getById(int id) {
        return map.get(id);
    }
}
