package com.sctrcd.qzr.facts;

import java.util.HashSet;
import java.util.Set;

public class Question {

    private final String key;
    private final String question;
    private final Set<Option> options = new HashSet<>();
    
    public Question(String key, String question) {
        this.key = key;
        this.question = question;
    }

    public String getKey() {
        return key;
    }

    public String getQuestion() {
        return question;
    }

    public Set<Option> getOptions() {
        return options;
    }
    
    public void addOption(Option option) {
        this.options.add(option);
    }
    
    public void addOption(String key, String value) {
        addOption(new Option(key, value));
    }

    public String toString() {
        return "Question: { key=\"" + key + "\", question=\"" + question + "\", options=[" + options + "] }";
    }
    
}
