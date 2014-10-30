package com.sctrcd.qzr.services;

import java.util.Collection;

import com.sctrcd.qzr.facts.Answer;
import com.sctrcd.qzr.facts.HrMax;
import com.sctrcd.qzr.facts.Known;
import com.sctrcd.qzr.facts.NextQuestion;
import com.sctrcd.qzr.facts.Question;
import com.sctrcd.qzr.facts.QuizState;

public interface QuizService {

    Collection<Question> getQuestions();

    Question getQuestion(String key);

    NextQuestion getNextQuestion();

    Collection<Known<?>> getKnowns();
    
    Collection<Answer> getAnswers();

    Answer getAnswer(String key);
    
    void answer(Answer answer);

    void answer(Known<?> known);

    QuizState getQuizState();

    HrMax getHrMax();

}