package com.sctrcd.qzr.facts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 
 * 
 * @author Stephen Masters
 */
public class QuizState {

    private List<Question> questions;
    private List<Known<?>> knowns;
    
    public QuizState() {
        this.knowns = new ArrayList<>();
        this.questions = new ArrayList<>();
    }
    
    public QuizState(Collection<Known<?>> knowns, Collection<Question> questions) {
        this.knowns = new ArrayList<>(knowns);
        this.questions = new ArrayList<>(questions);
    }

    public List<Known<?>> getKnowns() {
        return knowns;
    }

    public void setKnowns(List<Known<?>> knowns) {
        this.knowns = knowns;
    }

    public void add(Known<?> known) {
        if (this.knowns == null) this.knowns = new ArrayList<>();
        this.knowns.add(known);
    }
    
    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void add(Question question) {
        if (this.questions == null) this.questions = new ArrayList<>();
        this.questions.add(question);
    }

    
}
