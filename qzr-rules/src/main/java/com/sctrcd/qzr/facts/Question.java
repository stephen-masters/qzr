package com.sctrcd.qzr.facts;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Stephen Masters
 */
public class Question {

    private final String key;
    private final String question;
    private final Set<Option> options = new HashSet<>();
    private final AnswerType answerType;
    private Answer answer;
    
    public Question(String key, String question) {
        this.key = key;
        this.question = question;
        this.answerType = AnswerType.STRING;
    }
    
    public Question(String key, String question, AnswerType answerType) {
        this.key = key;
        this.question = question;
        this.answerType = answerType;
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

    public AnswerType getAnswerType() {
        return answerType;
    }
    
    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public String toString() {
        return "Question: { key=\"" + key + "\", question=\"" + question + "\", options=[" + options + "] }";
    }
    
}
