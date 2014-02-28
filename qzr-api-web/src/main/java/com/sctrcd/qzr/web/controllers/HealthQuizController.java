package com.sctrcd.qzr.web.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sctrcd.qzr.facts.KnownList;
import com.sctrcd.qzr.facts.QuestionList;

@RequestMapping("/api/quizzes/health")
public interface HealthQuizController {

    @RequestMapping(value = "questions", 
            method = RequestMethod.GET, 
            produces = "application/json")
    public @ResponseBody QuestionList getQuestions();
    
    @RequestMapping(value = "knowns", 
            method = RequestMethod.GET, 
            produces = "application/json")
    public @ResponseBody KnownList getKnowns();
    
}
