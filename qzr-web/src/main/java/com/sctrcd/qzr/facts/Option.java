package com.sctrcd.qzr.facts;

/**
 * A {@link Question} can define a list of options which can be used as answers.
 * These comprise a key, which will be used to identify the option internally,
 * and a value, which is a more human-readable value.
 * 
 * @author Stephen Masters
 */
public class Option {

    private String key;

    private String text;

    public Option() {
    }

    public Option(String key, String text) {
        this.key = key;
        this.text = text;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object that) {
        if (that == null)
            return false;
        if (that instanceof Option) {
            return this.key.equals(((Option) that).getKey());
        }
        return false;
    }

    public String toString() {
        return "Option: { key=" + key + ", text=" + text + " }";
    }

}
