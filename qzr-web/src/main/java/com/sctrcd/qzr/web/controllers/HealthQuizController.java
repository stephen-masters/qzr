package com.sctrcd.qzr.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sctrcd.qzr.facts.Known;
import com.sctrcd.qzr.facts.KnownList;
import com.sctrcd.qzr.facts.Question;
import com.sctrcd.qzr.facts.QuestionList;
import com.sctrcd.qzr.facts.QuizState;
import com.sctrcd.qzr.services.HealthQuizService;

@RestController
@RequestMapping("/api/quizzes/health")
public class HealthQuizController {

    private static Logger log = LoggerFactory.getLogger(HealthQuizController.class);

    @Autowired
    private HealthQuizService svc;

    public HealthQuizController() {
    }

    @RequestMapping(value = "questions", method = RequestMethod.GET, produces = "application/json")
    public QuestionList getQuestions() {
        QuestionList questions = new QuestionList(svc.getQuestions());

        log.debug("Questions: " + questions);

        return questions;
    }

    @RequestMapping(value = "questions/{key}", method = RequestMethod.GET, produces = "application/json")
    public Question getQuestion(@PathVariable(value = "key") String key) {
        Question question = svc.getQuestion(key);

        log.debug("Question: " + question);

        return question;
    }

    @RequestMapping(value = "knowns", method = RequestMethod.GET, produces = "application/json")
    public KnownList getKnowns() {
        KnownList knowns = new KnownList(svc.getKnowns());

        log.debug("Knowns: " + knowns);

        return knowns;
    }

    @RequestMapping(value = "quizstate", method = RequestMethod.GET, produces = "application/json")
    public QuizState getQuizState() {
        QuizState quizState = svc.getQuizState();
        return quizState;
    }

    @RequestMapping(value = "questions/{key}/answer", method = RequestMethod.POST, produces = "application/json")
    public QuizState answer(@PathVariable(value = "key") String key,
            @RequestParam(value = "answer", required = false) String answer) {

        log.debug("Answer to question: key=" + key + ", answer=" + answer);

        if (answer != null) {
            svc.answer(new Known<String>(key, answer));
        }
        QuizState quizState = svc.getQuizState();
        return quizState;
    }

}
