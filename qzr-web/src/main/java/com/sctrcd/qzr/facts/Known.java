package com.sctrcd.qzr.facts;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * A generic piece of 'known' information. The system learns by buiding up known
 * facts.
 * 
 * @author Stephen Masters
 *
 * @param <T>
 *            The class of the value.
 */
@XmlRootElement(name = "known")
public class Known<T> {

    private String key;
    private T value;
    private DateTime when;

    public Known() {
    }

    public Known(String key, T value) {
        this.key = key;
        this.value = value;
        this.when = DateTime.now(DateTimeZone.UTC);
    }

    public Known(String key, T value, DateTime when) {
        this.key = key;
        this.value = value;
        this.when = when;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public DateTime getWhen() {
        return when;
    }

    public void setWhen(DateTime when) {
        this.when = when;
    }

    public String toString() {
        return this.getClass().getSimpleName() + ": { key=\"" + key
                + "\", value=\"" + value + "\" }";
    }

}
