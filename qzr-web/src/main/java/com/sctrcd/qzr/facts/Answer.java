package com.sctrcd.qzr.facts;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * An answer to a question. The key is the same as the question for which this
 * is an answer.
 * 
 * @author Stephen Masters
 */
@XmlRootElement(name = "known")
public class Answer {

    private String key;
    private String value;
    private Question question;
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

    public Answer(String key, String value, Question question, DateTime when) {
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

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
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
