package com.sctrcd.qzr.services;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.sctrcd.beans.BeanPropertyFilter;
import com.sctrcd.drools.FactFinder;
import com.sctrcd.drools.monitoring.LoggingAgendaEventListener;
import com.sctrcd.drools.monitoring.LoggingRuleRuntimeEventListener;
import com.sctrcd.qzr.facts.Answer;
import com.sctrcd.qzr.facts.HrMax;
import com.sctrcd.qzr.facts.Known;
import com.sctrcd.qzr.facts.Question;

/**
 * This service encapsulates a long running Drools {@link KieSession}. It is
 * scoped to an HTTP session, so that a user can have a long-running interaction
 * with a Drools session that is dedicated to them. This makes it possible to
 * gradually build up knowledge about that user.
 * <p>
 * The idea is that the rules will determine questions to ask based on what is
 * already known about a user. As answers are provided, the knowledge base
 * develops and different questions are asked.
 * </p>
 * 
 * TODO - The agenda event publisher is publishing to a topic which is streamed
 * to all users. It's okay for a bit of local testing fun, but would obviously 
 * be a good idea to modify it so that each client gets their own queue.
 * 
 * @author Stephen Masters
 */
@Service
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class HrMaxQuizService {

    private static Logger log = LoggerFactory.getLogger(HrMaxQuizService.class);
            
    private final KieSession kieSession;
    
    private final PublishingAgendaEventListener agendaEventPublisher;
    private final AgendaEventListener agendaEventListener;
    private final RuleRuntimeEventListener ruleRuntimeEventListener;
    
    private final FactFinder<Question> questionFinder = new FactFinder<>(Question.class);
    private final FactFinder<Known<?>> knownFinder = new FactFinder<>(Known.class);
    private final FactFinder<Answer> answerFinder = new FactFinder<>(Answer.class);
    private final FactFinder<HrMax> hrMaxFinder = new FactFinder<>(HrMax.class);
    
    @Autowired
    public HrMaxQuizService(KieContainer kieContainer, SimpMessagingTemplate template) {
        
        log.info("Initialising a new quiz session.");
        
        this.kieSession = kieContainer.newKieSession("HrmaxSession");
        
        this.agendaEventPublisher = new PublishingAgendaEventListener(template);
        this.agendaEventListener = new LoggingAgendaEventListener();
        this.ruleRuntimeEventListener = new LoggingRuleRuntimeEventListener();

        this.kieSession.addEventListener(agendaEventPublisher);
        this.kieSession.addEventListener(agendaEventListener);
        this.kieSession.addEventListener(ruleRuntimeEventListener);
        
        this.kieSession.fireAllRules();
    }

    public List<Question> allQuestions() {
        List<Question> questions = questionFinder.findFacts(kieSession);
        List<Question> answeredQuestions = answerFinder.findFacts(kieSession)
                .stream()
                .map(answer -> answer.getQuestion())
                .collect(Collectors.toList());
        
        answeredQuestions.addAll(questions);
        
        for (Question q : answeredQuestions) {
            System.out.println("Question: " + q);
        }
        
        return answeredQuestions;
    }
    
    public Question getNextQuestion() {
        List<Question> questions = questionFinder.findFacts(kieSession);
        
        if (questions.size() == 0) return null;
        if (questions.size() > 1) {
            log.error("Multiple (" + questions.size() + ") questions found.");
        }
        Question nextQuestion = questions.get(0);
        return nextQuestion;
    }
    
    public Question getQuestion(String key) {
        List<Question> questions = questionFinder.findFacts(kieSession, 
                new BeanPropertyFilter("key", key));
        List<Answer> answers = answerFinder.findFacts(kieSession, 
                new BeanPropertyFilter("key", key));
        
        if (questions.size() == 1) return questions.get(0);
        if (answers.size() == 1) return answers.get(0).getQuestion();
        
        if (questions.size() > 1) {
            log.error("Multiple questions found with same key: " + key);
        }
        
        return null;
    }

    public Collection<Known<?>> getKnowns() {
        Collection<Known<?>> knowns = knownFinder.findFacts(kieSession);
        return knowns;
    }
    
    public Collection<Answer> getAnswers() {
        Collection<Answer> answers = answerFinder.findFacts(kieSession);
        return answers;
    }
    
    public Answer getAnswer(String key) {
        Collection<Answer> answers = answerFinder.findFacts(kieSession, 
                new BeanPropertyFilter("key", key));
        
        if (answers == null || answers.size() == 0) {
            return null;
        } else if (answers.size() > 1) {
            log.error("Multiple answers found with same key: " + key);
        }
        
        return answers.iterator().next();
    }

    public Answer answer(Answer answer) {
        kieSession.insert(answer);
        kieSession.fireAllRules();
        return answer;
    }
    
    public void retractAnswer(String key) {
        answerFinder.deleteFacts(kieSession, new BeanPropertyFilter("key", key));
        kieSession.fireAllRules();
    }
    
    public void answer(Known<?> known) {
        kieSession.insert(known);
        kieSession.fireAllRules();
    }

    public HrMax getHrMax() {
        Collection<HrMax> answers = hrMaxFinder.findFacts(kieSession);
        
        if (answers == null || answers.size() == 0) {
            return null;
        } else if (answers.size() > 1) {
            log.error("Multiple HR max results found.");
        }
        
        return answers.iterator().next();
    }
    
}
