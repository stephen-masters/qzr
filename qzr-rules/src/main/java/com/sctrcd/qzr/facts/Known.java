package com.sctrcd.qzr.facts;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "known")
public class Known<T> {

    private String key;
    private T value;

    public Known() {
    }

    public Known(String key, T value) {
        this.key = key;
        this.value = value;
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

    public String toString() {
        return this.getClass().getSimpleName() 
                + ": { key=\"" + key + "\", value=\"" + value + "\" }";
    }

}
