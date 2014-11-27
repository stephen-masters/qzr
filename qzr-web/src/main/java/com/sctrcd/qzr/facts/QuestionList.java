package com.sctrcd.qzr.facts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A list of {@link Question}.
 * 
 * @author Stephen Masters
 */
@XmlRootElement(name = "questions")
public class QuestionList {

    private List<Question> questions;

    public QuestionList() {

    }

    public QuestionList(Collection<Question> questions) {
        this.questions = new ArrayList<>(questions);
    }

    @XmlElement(name = "question")
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

    public String toString() {
        StringBuilder sb = new StringBuilder("QuestionList: { questions=[ ");
        for (Question q : questions) {
            sb.append(q.toString());
            sb.append(",  ");
        }
        if (questions == null || questions.size() == 0) {
            return sb.toString() + " ]}";
        } else {
            return sb.substring(0, sb.length() - 3) + " ]}";
        }
    }
    
}
