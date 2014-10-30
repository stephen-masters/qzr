package com.sctrcd.qzr.facts;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

@XmlRootElement(name = "known")
public class Answer {

    private String key;
    private String value;
    private DateTime when;

    public Answer() {
    }

    public Answer(String key, String value) {
        this(key, value, DateTime.now(DateTimeZone.UTC));
    }

    public Answer(String key, String value, DateTime when) {
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public DateTime getWhen() {
        return when;
    }

    public void setWhen(DateTime when) {
        this.when = when;
    }

    public String toString() {
        return this.getClass().getSimpleName() 
                + ": { key=\"" + key + "\", value=\"" + value + "\" }";
    }

}
