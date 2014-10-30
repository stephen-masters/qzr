package com.sctrcd.qzr.web.controllers;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sctrcd.qzr.facts.Answer;
import com.sctrcd.qzr.facts.HrMax;
import com.sctrcd.qzr.facts.KnownList;
import com.sctrcd.qzr.facts.NextQuestion;
import com.sctrcd.qzr.facts.Question;
import com.sctrcd.qzr.facts.QuizState;
import com.sctrcd.qzr.services.QuizService;
import com.sctrcd.qzr.web.resources.AnswerResource;
import com.sctrcd.qzr.web.resources.AnswerResourceAssembler;
import com.sctrcd.qzr.web.resources.ErrorMessage;
import com.sctrcd.qzr.web.resources.NotFoundException;
import com.sctrcd.qzr.web.resources.QuestionResource;
import com.sctrcd.qzr.web.resources.QuestionResourceAssembler;

@RestController
@RequestMapping("/api/quizzes/health")
public class HealthQuizController {

    private static Logger log = LoggerFactory.getLogger(HealthQuizController.class);

    @Autowired
    private QuizService svc;
    
    @Autowired
    private AnswerResourceAssembler answerAssembler;
    
    @Autowired
    private QuestionResourceAssembler questionAssembler;

    public HealthQuizController() {
    }

    @RequestMapping(value = "/questions", method = RequestMethod.GET, produces = "application/json")
    public HttpEntity<List<QuestionResource>> getQuestions() {
        Collection<Question> questions = svc.getQuestions();

        log.debug("Questions: " + questions);

        List<QuestionResource> resources = questionAssembler.toResources(questions);

        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @RequestMapping(value = "/questions/{key}", method = RequestMethod.GET, produces = "application/json")
    public HttpEntity<QuestionResource> getQuestion(@PathVariable(value = "key") String key) {
        Question question = svc.getQuestion(key);

        log.debug("Question: " + question);
        
        QuestionResource resource = questionAssembler.toResource(question);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/questions/next", method = RequestMethod.GET, produces = "application/json")
    public HttpEntity<QuestionResource> getNextQuestion(@PathVariable(value = "key") String key) {
        NextQuestion question = svc.getNextQuestion();

        log.debug("Question: " + question.getQuestion());

        QuestionResource resource = questionAssembler.toResource(question.getQuestion());

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @RequestMapping(value = "/knowns", method = RequestMethod.GET, produces = "application/json")
    public KnownList getKnowns() {
        KnownList knowns = new KnownList(svc.getKnowns());

        log.debug("Knowns: " + knowns);

        return knowns;
    }
    
    @RequestMapping(value = "/answers", method = RequestMethod.GET, produces = "application/json")
    public HttpEntity<List<AnswerResource>> getAnswers() {
        Collection<Answer> answers = svc.getAnswers();

        log.debug("Answers: " + answers);

        List<AnswerResource> answerResources = answerAssembler.toResources(answers);

        return new ResponseEntity<>(answerResources, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/answers/{key}", method = RequestMethod.GET, produces = "application/json")
    public HttpEntity<AnswerResource> getAnswer(String key) {
        Answer answer = svc.getAnswer(key);
        
        AnswerResource resource = answerAssembler.toResource(answer);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @RequestMapping(value = "/answers", method = RequestMethod.PUT)
    public HttpEntity<AnswerResource> answer(@RequestBody(required = false) AnswerResource answer) {

        log.debug("Answer to question: key=" + answer.getKey() + ", answer=" + answer.getValue());

        //Known<String> known = new Known<String>(answer.getKey(), answer.getValue(), DateTime.now(DateTimeZone.UTC));
        Answer fact = new Answer(answer.getKey(), answer.getValue());
        
        svc.answer(fact);
        
        AnswerResource answerResource = answerAssembler.toResource(fact);

        return new ResponseEntity<>(answerResource, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/results/hrmax", method = RequestMethod.GET, produces = "application/json")
    public HrMax getHrMax() {
        HrMax hrMax = svc.getHrMax();
        
        if (hrMax == null) throw new NotFoundException("HR max has yet to be calculated.");
        
        return hrMax;
    }
    
    @RequestMapping(value = "quizstate", method = RequestMethod.GET, produces = "application/json")
    public QuizState getQuizState() {
        QuizState quizState = svc.getQuizState();
        return quizState;
    }
    
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleNotFoundException(NotFoundException e, HttpServletRequest req) {
        return new ErrorMessage(e);
    }

}
