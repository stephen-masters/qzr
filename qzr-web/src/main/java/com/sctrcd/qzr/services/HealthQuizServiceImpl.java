package com.sctrcd.qzr.services;

import java.util.Collection;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.sctrcd.beans.BeanPropertyFilter;
import com.sctrcd.drools.FactFinder;
import com.sctrcd.drools.monitoring.TrackingAgendaEventListener;
import com.sctrcd.drools.monitoring.TrackingWorkingMemoryEventListener;
import com.sctrcd.qzr.facts.Answer;
import com.sctrcd.qzr.facts.HrMax;
import com.sctrcd.qzr.facts.Known;
import com.sctrcd.qzr.facts.NextQuestion;
import com.sctrcd.qzr.facts.Question;
import com.sctrcd.qzr.facts.QuizState;

@Service
@Scope(value="session", proxyMode=ScopedProxyMode.INTERFACES)
public class HealthQuizServiceImpl implements QuizService {

    private static Logger log = LoggerFactory.getLogger(HealthQuizServiceImpl.class);
            
    public KieSession kieSession;
    
    private TrackingAgendaEventListener agendaEventListener;
    private TrackingWorkingMemoryEventListener workingMemoryEventListener;
    
    private FactFinder<NextQuestion> nextQuestionFinder = new FactFinder<>(NextQuestion.class);
    private FactFinder<Question> valueQuestionFinder = new FactFinder<>(Question.class);
    private FactFinder<Known<?>> knownFinder = new FactFinder<>(Known.class);
    private FactFinder<Answer> answerFinder = new FactFinder<>(Answer.class);
    private FactFinder<HrMax> hrMaxFinder = new FactFinder<>(HrMax.class);
    
    @Autowired
    public HealthQuizServiceImpl(
            @Qualifier("healthQuizKieContainer") KieContainer kieContainer) {
        
        log.info("Initialising a new quiz session.");
        
        kieSession = kieContainer.newKieSession();
        
        agendaEventListener = new TrackingAgendaEventListener();
        workingMemoryEventListener = new TrackingWorkingMemoryEventListener();

        kieSession.addEventListener(agendaEventListener);
        kieSession.addEventListener(workingMemoryEventListener);
        
        kieSession.fireAllRules();
    }
    
    @Override
    public Collection<Question> getQuestions() {
        Collection<Question> questions = valueQuestionFinder.findFacts(kieSession);
        return questions;
    }
    
    @Override
    public Question getQuestion(String key) {
        Collection<Question> questions = valueQuestionFinder.findFacts(kieSession, 
                new BeanPropertyFilter("key", key));
        
        if (questions == null || questions.size() == 0) {
            return null;
        } else if (questions.size() > 1) {
            log.error("Multiple questions found with same key: " + key);
        }
        
        return questions.iterator().next();
    }
    
    @Override
    public NextQuestion getNextQuestion() {
        Collection<NextQuestion> questions = nextQuestionFinder.findFacts(kieSession);
        
        if (questions == null || questions.size() == 0) {
            return null;
        } else if (questions.size() > 1) {
            log.error("Multiple 'next' questions found.");
        }
        
        return questions.iterator().next();
    }

    @Override
    public Collection<Known<?>> getKnowns() {
        Collection<Known<?>> knowns = knownFinder.findFacts(kieSession);
        return knowns;
    }
    
    @Override
    public Collection<Answer> getAnswers() {
        Collection<Answer> answers = answerFinder.findFacts(kieSession);
        return answers;
    }
    
    @Override
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

    @Override
    public void answer(Answer answer) {
        kieSession.insert(answer);
        kieSession.fireAllRules();
    }
    
    @Override
    public void answer(Known<?> known) {
        kieSession.insert(known);
        kieSession.fireAllRules();
    }

    @Override
    public HrMax getHrMax() {
        Collection<HrMax> answers = hrMaxFinder.findFacts(kieSession);
        
        if (answers == null || answers.size() == 0) {
            return null;
        } else if (answers.size() > 1) {
            log.error("Multiple HR max results found.");
        }
        
        return answers.iterator().next();
    }
    
    @Override
    public QuizState getQuizState() {
        QuizState quiz = new QuizState(getKnowns(), getQuestions());
        return quiz;
    }
    
}
