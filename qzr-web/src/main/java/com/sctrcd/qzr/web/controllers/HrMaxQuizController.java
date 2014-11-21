package com.sctrcd.qzr.web.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

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
import com.sctrcd.qzr.facts.Known;
import com.sctrcd.qzr.facts.KnownList;
import com.sctrcd.qzr.facts.Question;
import com.sctrcd.qzr.services.HrMaxQuizServiceImpl;
import com.sctrcd.qzr.services.QuizService;
import com.sctrcd.qzr.web.resources.AnswerResource;
import com.sctrcd.qzr.web.resources.AnswerResourceAssembler;
import com.sctrcd.qzr.web.resources.BadRequestException;
import com.sctrcd.qzr.web.resources.ErrorMessage;
import com.sctrcd.qzr.web.resources.NotFoundException;
import com.sctrcd.qzr.web.resources.QuestionResource;
import com.sctrcd.qzr.web.resources.QuestionResourceAssembler;
import com.sctrcd.qzr.web.resources.QuizResource;

@RestController
@RequestMapping("/api/quizzes/health")
public class HrMaxQuizController {

    private static Logger log = LoggerFactory.getLogger(HrMaxQuizController.class);

    @Autowired
    private QuizService svc;
    @Autowired
    private AnswerResourceAssembler answerAssembler;
    @Autowired
    private QuestionResourceAssembler questionAssembler;

    public HrMaxQuizController() {
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public QuizResource getQuiz() throws NotFoundException {
        QuizResource quiz = new QuizResource("hrmax");

        quiz.add(linkTo(methodOn(HrMaxQuizController.class).getQuiz()).withSelfRel());
        quiz.add(linkTo(methodOn(HrMaxQuizController.class).getNextQuestion()).withRel("nextQuestion"));
        quiz.add(linkTo(methodOn(HrMaxQuizController.class).getAnswers()).withRel("answers"));
        quiz.add(linkTo(methodOn(HrMaxQuizController.class).getKnowns()).withRel("knowns"));
        quiz.add(linkTo(methodOn(HrMaxQuizController.class).getHrMax()).withRel("hrmax"));

        return quiz;
    }

    @RequestMapping(value = "/questions", method = RequestMethod.GET, produces = "application/json")
    public HttpEntity<List<QuestionResource>> getQuestions() throws NotFoundException {
        List<Question> questions = svc.allQuestions();

        log.debug("Questions: " + questions);

        List<QuestionResource> resources = questionAssembler.toResources(questions);

        return new ResponseEntity<>(resources, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/questions/next", method = RequestMethod.GET, produces = "application/json")
    public HttpEntity<QuestionResource> getNextQuestion() throws NotFoundException {
        Question question = svc.getNextQuestion();

        log.debug("Question: " + question);
        
        if (question == null) {
            throw new NotFoundException("NO_MORE_QUESTIONS", "No more questions.");
        }

        QuestionResource resource = questionAssembler.toResource(question);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @RequestMapping(value = "/questions/{key}", method = RequestMethod.GET, produces = "application/json")
    public HttpEntity<QuestionResource> getQuestion(@PathVariable(value = "key") String key) {
        Question question = svc.getQuestion(key);

        log.debug("Question: " + question);
        
        QuestionResource resource = questionAssembler.toResource(question);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/questions/{key}/answer", method = RequestMethod.PUT)
    public HttpEntity<AnswerResource> answer(
            @PathVariable(value = "key") String key, 
            @RequestBody(required = true) AnswerResource answer) throws BadRequestException {

        log.debug("Answer to question [" + key + "]: " + answer.getValue());

        if (answer.getValue() == null || "".equals(answer.getValue())) {
            throw new BadRequestException("Not a valid answer.");
        } 
        
        Answer fact = new Answer(key, answer.getValue());
        
        fact = svc.answer(fact);

        AnswerResource answerResource = answerAssembler.toResource(fact);

        return new ResponseEntity<>(answerResource, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/questions/{key}/skip", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void skip(
            @PathVariable(value = "key") String key) throws BadRequestException {

        log.debug("Skipping question [" + key + "].");
        
        Answer fact = new Answer(key, null);
        
        svc.answer(fact);
    }
    
    @RequestMapping(value = "/questions/{key}/answer", method = RequestMethod.GET, produces = "application/json")
    public HttpEntity<AnswerResource> getAnswer(String key) throws NotFoundException {
        Answer answer = svc.getAnswer(key);
        
        if (answer == null) {
            throw new NotFoundException("NO_ANSWER", "This question has not been answered yet.");
        }
        
        AnswerResource resource = answerAssembler.toResource(answer);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/questions/{key}/answer", method = RequestMethod.DELETE)
    public void retractAnswer(@PathVariable(value = "key") String key) {

        log.debug("Retracting answer to question [" + key + "]");

        svc.retractAnswer(key);
    }

    @RequestMapping(value = "/knowns", method = RequestMethod.GET, produces = "application/json")
    public Collection<Known<?>> getKnowns() {
        Collection<Known<?>> knowns = svc.getKnowns();

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
    
    @RequestMapping(value = "/results/hrmax", method = RequestMethod.GET, produces = "application/json")
    public HrMax getHrMax() throws NotFoundException {
        HrMax hrMax = svc.getHrMax();
        
        if (hrMax == null) throw new NotFoundException("HRMAX_NOT_FOUND", "HR max has yet to be calculated.");
        
        return hrMax;
    }
    
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handle(NotFoundException ex, HttpServletRequest req) {
        return new ErrorMessage(ex);
    }
    
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handle(BadRequestException ex, HttpServletRequest req) {
        return new ErrorMessage(ex);
    }

}
