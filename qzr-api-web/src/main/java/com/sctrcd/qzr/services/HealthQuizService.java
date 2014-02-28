package com.sctrcd.qzr.services;

import java.util.Collection;

import com.sctrcd.qzr.facts.Known;
import com.sctrcd.qzr.facts.Question;

public interface HealthQuizService {

    public Collection<Question> getQuestions();
    
    public Collection<Known<?>> getKnowns();
    
    public void answer(Known<?> known);
    
}
