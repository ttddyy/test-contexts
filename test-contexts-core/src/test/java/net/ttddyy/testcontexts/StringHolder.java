package net.ttddyy.testcontexts;

/**
 * Simple bean entry for integration test context.
 *
 * @author Tadaya Tsuyukubo
 */
public class StringHolder {

    private String value;

    public StringHolder() {
    }

    public StringHolder(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
