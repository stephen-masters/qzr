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
import com.sctrcd.qzr.facts.Known;
import com.sctrcd.qzr.facts.Question;
import com.sctrcd.qzr.facts.QuizState;
import com.sctrcd.qzr.facts.ValueQuestion;

@Service
@Scope(value="session", proxyMode=ScopedProxyMode.INTERFACES)
public class HealthQuizServiceImpl implements HealthQuizService {

    private static Logger log = LoggerFactory.getLogger(HealthQuizServiceImpl.class);
            
    public KieSession kieSession;
    
    private TrackingAgendaEventListener agendaEventListener;
    private TrackingWorkingMemoryEventListener workingMemoryEventListener;
    
    private FactFinder<Question> valueQuestionFinder = new FactFinder<>(ValueQuestion.class);
    private FactFinder<Known<?>> knownFinder = new FactFinder<>(Known.class);
    
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
    public Collection<Known<?>> getKnowns() {
        Collection<Known<?>> knowns = knownFinder.findFacts(kieSession);
        return knowns;
    }

    @Override
    public void answer(Known<?> known) {
        kieSession.insert(known);
        kieSession.fireAllRules();
    }

    @Override
    public QuizState getQuizState() {
        QuizState quiz = new QuizState(getKnowns(), getQuestions());
        return quiz;
    }
    
}
