package com.sctrcd.qzr.web.resources;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;
import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sctrcd.qzr.web.json.JsonJodaDateTimeSerializer;

@XmlRootElement(name = "answer")
public class AnswerResource extends ResourceSupport {

    private String key;
    private String value;
    private DateTime when;

    public AnswerResource() {
    }

    public AnswerResource(String key, String value) {
        this.key = key;
        this.value = value;
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

    @JsonSerialize(using = JsonJodaDateTimeSerializer.class)
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
