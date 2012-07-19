package net.ttddyy.testcontexts.samples.domain;

/**
 * Sample Domain Object
 *
 * @author Tadaya Tsuyukubo
 */
public class Account {
    private Integer id;
    private String name;
    private long amount;

    public Account() {
    }

    public Account(Integer id, String name, long amount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
