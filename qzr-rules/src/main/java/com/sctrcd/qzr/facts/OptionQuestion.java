package com.sctrcd.qzr.facts;

import java.util.HashSet;
import java.util.Set;

public class OptionQuestion extends Question {

    private Set<Option> options = new HashSet<>();
    
    public OptionQuestion() {
        super();
    }
    
    public OptionQuestion(String key, String question) {
        super(key, question);
    }

    public Set<Option> getOptions() {
        return options;
    }

    public void setOptions(Set<Option> options) {
        this.options = options;
    }
    
    public void addOption(Option option) {
        if (this.options == null) {
            this.options = new HashSet<>();
        }
        this.options.add(option);
    }
    
    public void addOption(String key, String value) {
        addOption(new Option(key, value));
    }

    public String toString() {
        return "Question: { key=\"" + getKey() + "\", question=\"" + getQuestion() + "\", options=[" + options + "] }";
    }
    
}
