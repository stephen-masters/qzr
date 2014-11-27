package com.sctrcd.qzr.web.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
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
import com.sctrcd.qzr.facts.Option;
import com.sctrcd.qzr.facts.Question;
import com.sctrcd.qzr.services.HrMaxQuizService;
import com.sctrcd.qzr.web.resources.AnswerResource;
import com.sctrcd.qzr.web.resources.AnswerResourceAssembler;
import com.sctrcd.qzr.web.resources.BadRequestException;
import com.sctrcd.qzr.web.resources.ErrorMessage;
import com.sctrcd.qzr.web.resources.NotFoundException;
import com.sctrcd.qzr.web.resources.QuestionResource;
import com.sctrcd.qzr.web.resources.QuestionResourceAssembler;
import com.sctrcd.qzr.web.resources.QuizResource;

/**
 * This is the controller providing the REST API for the HR Max quiz.
 * <p>
 * For those unfamiliar with it, this API makes use of Spring HATEOAS to provide
 * hypertext links in the JSON responses. A number of methods return objects
 * which implement {@link ResourceSupport}, which means that the JSON returned
 * will contain 'self' links to those resources and other links to operations
 * which are available, relating to them. For instance, a
 * {@link QuestionResource} provides a link to the endpoint where an answer can
 * be provided. An {@link AnswerResource} may have a link back to the
 * {@link QuestionResource} that it was a response to.
 * </p>
 * <p>
 * You will therefore notice various <code>linkTo(methodOn(...))</code>
 * statements. Also resource assemblers such as <code>answerAssembler</code>
 * &amp; <code>questionAssembler</code>, make it a little bit less effort to
 * build these resource objects.
 * </p>
 * <p>
 * You may notice that elsewhere in the application, constructor injection is
 * used, whereas here, the fields are <code>@Autowired</code>. This is because
 * the {@link HrMaxQuizService} is session-scoped, which means that it can't be
 * injected at the time the controller is instantiated. My usual preference is
 * to go with final fields and constructor injection, as this makes it a lot
 * easier to inject mocks when unit testing.
 * </p>
 * 
 * @author Stephen Masters
 */
@RestController
@RequestMapping("/api/quizzes/health")
public class HrMaxQuizController {

    private static Logger log = LoggerFactory.getLogger(HrMaxQuizController.class);

    @Autowired
    private HrMaxQuizService svc;
    @Autowired
    private AnswerResourceAssembler answerAssembler;
    @Autowired
    private QuestionResourceAssembler questionAssembler;

    public HrMaxQuizController() {
    }

    /**
     * The 'home' of the HRMax quiz API. By GETing this, you should see links to
     * the various operations which are available. i.e.:
     * 
     * <pre>
     * curl http://127.0.0.1:40080/api/quizzes/health/
     * </pre>
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public QuizResource getQuiz() {
        QuizResource quiz = new QuizResource("hrmax");

        try {
            quiz.add(linkTo(methodOn(HrMaxQuizController.class).getQuiz()).withSelfRel());
            quiz.add(linkTo(methodOn(HrMaxQuizController.class).getNextQuestion()).withRel("nextQuestion"));
            quiz.add(linkTo(methodOn(HrMaxQuizController.class).getAnswers()).withRel("answers"));
            quiz.add(linkTo(methodOn(HrMaxQuizController.class).getKnowns()).withRel("knowns"));
            quiz.add(linkTo(methodOn(HrMaxQuizController.class).getHrMax()).withRel("hrmax"));
        } catch (NotFoundException e) {
            // Ignore ... the exception can't actually be thrown.
        }

        return quiz;
    }

    /**
     * Get a list of all questions which have been asked so far.
     * 
     * @return A list of {@link QuestionResource}.
     */
    @RequestMapping(value = "/questions", method = RequestMethod.GET, produces = "application/json")
    public HttpEntity<List<QuestionResource>> getQuestions() {
        
        log.debug("Request received for all questions.");
        
        List<Question> questions = svc.allQuestions();

        log.debug("Questions: " + questions);

        List<QuestionResource> resources = questionAssembler.toResources(questions);

        return new ResponseEntity<>(resources, HttpStatus.OK);
    }
    
    /**
     * Get hold of the next question to be answered. If there are no more
     * questions being asked, then this will return a 404 and a message in the
     * JSON response to indicate that there are no more questions.
     * 
     * @return A {@link QuestionResource}.
     * @throws NotFoundException
     *             If there are no more questions.
     */
    @RequestMapping(value = "/questions/next", method = RequestMethod.GET, produces = "application/json")
    public HttpEntity<QuestionResource> getNextQuestion() 
            throws NotFoundException {
        
        log.debug("Request received for next question.");
        
        Question question = svc.getNextQuestion();

        log.debug("The next question is: " + question);
        
        if (question == null) {
            throw new NotFoundException("NO_MORE_QUESTIONS", "No more questions.");
        }

        QuestionResource resource = questionAssembler.toResource(question);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    /**
     * Find a specific question which has been asked.
     * 
     * @param key The unique key for the question.
     * @return
     * @throws NotFoundException
     */
    @RequestMapping(value = "/questions/{key}", method = RequestMethod.GET, produces = "application/json")
    public HttpEntity<QuestionResource> getQuestion(@PathVariable(value = "key") String key) 
            throws NotFoundException {
        
        Question question = svc.getQuestion(key);

        log.debug("Found question for key [" + key + "]: " + question);
        
        if (question == null) {
            throw new NotFoundException("NOT_FOUND", "Question [" + key + "] cannot be found.");
        }
        
        QuestionResource resource = questionAssembler.toResource(question);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }
    
    /**
     * Find a specific question which has been asked.
     * 
     * @param key
     *            The unique key of the question being answered.
     * @param answer
     *            The details of the answer.
     * @return The answer.
     * @throws BadRequestException
     *             If the answer can't be parsed. Perhaps it's supposed to be a
     *             date, but the format is incorrect. Or an invalid {@link Option}.
     */
    @RequestMapping(value = "/questions/{key}/answer", method = RequestMethod.PUT)
    public HttpEntity<AnswerResource> answer(
            @PathVariable(value = "key") String key, 
            @RequestBody(required = true) AnswerResource answer) 
                    throws BadRequestException {

        log.debug("Answer to question [" + key + "]: " + answer.getValue());

        if (answer.getValue() == null || "".equals(answer.getValue())) {
            throw new BadRequestException("Not a valid answer.");
        } 
        
        Answer fact = new Answer(key, answer.getValue());
        
        fact = svc.answer(fact);

        AnswerResource answerResource = answerAssembler.toResource(fact);

        return new ResponseEntity<>(answerResource, HttpStatus.OK);
    }
    
    /**
     * This is called to indicate that the question will not be answered.
     * 
     * @param key The unique key of the question.
     */
    @RequestMapping(value = "/questions/{key}/skip", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void skip(
            @PathVariable(value = "key") String key) {

        log.debug("Skipping question [" + key + "].");
        
        Answer fact = new Answer(key, null);
        
        svc.answer(fact);
    }
    
    /**
     * Find the answer to a specific question.
     * 
     * @param key
     *            The unique key of the question.
     * @return The {@link AnswerResource}.
     * @throws NotFoundException
     *             If the question cannot be found or no answer has yet been
     *             provided to it.
     */
    @RequestMapping(value = "/questions/{key}/answer", method = RequestMethod.GET, produces = "application/json")
    public HttpEntity<AnswerResource> getAnswer(String key) throws NotFoundException {
        Answer answer = svc.getAnswer(key);
        
        if (answer == null) {
            throw new NotFoundException("NO_ANSWER", "This question has not been answered yet.");
        }
        
        AnswerResource resource = answerAssembler.toResource(answer);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }
    
    /**
     * Delete an answer previously provided to a question.
     * 
     * @param key
     *            The unique key of the question.
     */
    @RequestMapping(value = "/questions/{key}/answer", method = RequestMethod.DELETE)
    public void retractAnswer(@PathVariable(value = "key") String key) {

        log.debug("Retracting answer to question [" + key + "]");

        svc.retractAnswer(key);
    }

    /**
     * Find all the {@link Known} facts which have so far been derived.
     * 
     * @return A list of {@link Known}.
     */
    @RequestMapping(value = "/knowns", method = RequestMethod.GET, produces = "application/json")
    public Collection<Known<?>> getKnowns() {
        Collection<Known<?>> knowns = svc.getKnowns();

        log.debug("Knowns: " + knowns);

        return knowns;
    }
    
    /**
     * Find all the answers which have been provided so far.
     * 
     * @return A list of {@link AnswerResource}.
     */
    @RequestMapping(value = "/answers", method = RequestMethod.GET, produces = "application/json")
    public HttpEntity<List<AnswerResource>> getAnswers() {
        Collection<Answer> answers = svc.getAnswers();

        log.debug("Answers: " + answers);

        List<AnswerResource> answerResources = answerAssembler.toResources(answers);

        return new ResponseEntity<>(answerResources, HttpStatus.OK);
    }
    
    /**
     * Returns the {@link HrMax} if it has been calculated.
     * @return The calculated {@link HrMax} details.
     * @throws NotFoundException If HR Max has not yet been determined.
     */
    @RequestMapping(value = "/results/hrmax", 
                    method = RequestMethod.GET, 
                    produces = "application/json")
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
