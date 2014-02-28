package com.sctrcd.qzr.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sctrcd.qzr.facts.KnownList;
import com.sctrcd.qzr.facts.QuestionList;
import com.sctrcd.qzr.services.HealthQuizService;

@Controller
public class HealthQuizControllerImpl implements HealthQuizController {

    private static Logger log = LoggerFactory
            .getLogger(HealthQuizControllerImpl.class);

    @Autowired
    private HealthQuizService svc;

    public HealthQuizControllerImpl() {
    }

    @Override
    public QuestionList getQuestions() {
        QuestionList questions = new QuestionList(svc.getQuestions());

        log.debug("Questions: " + questions);

        return questions;
    }

    @Override
    public KnownList getKnowns() {
        KnownList knowns = new KnownList(svc.getKnowns());

        log.debug("Knowns: " + knowns);

        return knowns;
    }
}
