package com.sctrcd.qzr.services;

import java.util.Collection;
import java.util.List;

import com.sctrcd.qzr.facts.Answer;
import com.sctrcd.qzr.facts.HrMax;
import com.sctrcd.qzr.facts.Known;
import com.sctrcd.qzr.facts.Question;

public interface QuizService {

    Question getNextQuestion();

    Question getQuestion(String key);

    Collection<Known<?>> getKnowns();

    Collection<Answer> getAnswers();

    Answer getAnswer(String key);

    Answer answer(Answer answer);

    void answer(Known<?> known);

    HrMax getHrMax();

    void retractAnswer(String key);

    List<Question> allQuestions();

}