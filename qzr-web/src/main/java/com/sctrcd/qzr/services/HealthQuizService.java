package com.sctrcd.qzr.services;

import java.util.Collection;

import com.sctrcd.qzr.facts.Known;
import com.sctrcd.qzr.facts.Question;
import com.sctrcd.qzr.facts.QuizState;

public interface HealthQuizService {

    Collection<Question> getQuestions();
    
    Question getQuestion(String key);
    
    Collection<Known<?>> getKnowns();
    
    void answer(Known<?> known);
    
    QuizState getQuizState();
    
}
