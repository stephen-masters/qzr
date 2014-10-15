package com.sctrcd.qzr.facts;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "question")
public abstract class Question {

    private String key;
    private String question;
    
    public Question() {
    }
    
    public Question(String key, String question) {
        this.key = key;
        this.question = question;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean equals(Object that) {
        if (that == null) return false;
        if (that instanceof Question) {
            return this.key.equals(((Question)that).key);
        } else {
            return false;
        }
    }
    
    public String toString() {
        return "Question: { key=\"" + key + "\", question=\"" + question + "\" }";
    }
    
}
