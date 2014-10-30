package com.sctrcd.qzr.facts;

public class NextQuestion {

    private Question question;

    public NextQuestion() {
    }

    public NextQuestion(Question question) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
    
    public String toString() {
        return NextQuestion.class.getSimpleName() + ": { question=" + question + " }";
    }

}
