package com.sctrcd.qzr.services;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.sctrcd.drools.FactFinder;
import com.sctrcd.drools.monitoring.TrackingAgendaEventListener;
import com.sctrcd.drools.monitoring.TrackingWorkingMemoryEventListener;
import com.sctrcd.drools.spring.DefaultKieSessionBean;
import com.sctrcd.drools.spring.KieContainerBean;
import com.sctrcd.drools.spring.KieServicesBean;
import com.sctrcd.drools.spring.KieSessionBean;
import com.sctrcd.qzr.facts.Known;
import com.sctrcd.qzr.facts.Question;
import com.sctrcd.qzr.facts.ValueQuestion;

@Service
@Scope(value="session", proxyMode=ScopedProxyMode.INTERFACES)
public class HealthQuizServiceImpl implements HealthQuizService {

    private static Logger log = LoggerFactory.getLogger(HealthQuizServiceImpl.class);
            
    public KieSessionBean kieSession;
    
    private TrackingAgendaEventListener agendaEventListener;
    private TrackingWorkingMemoryEventListener workingMemoryEventListener;
    
    private FactFinder<Question> valueQuestionFinder = new FactFinder<>(ValueQuestion.class);
    private FactFinder<Known<?>> knownFinder = new FactFinder<>(Known.class);
    
    @Autowired
    public HealthQuizServiceImpl(
            @Qualifier("healthQuizKieServices") KieServicesBean kieServices, 
            @Qualifier("healthQuizKieContainer") KieContainerBean kieContainer) {
        
        kieSession = new DefaultKieSessionBean(kieServices, kieContainer);
        
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
    public Collection<Known<?>> getKnowns() {
        Collection<Known<?>> knowns = knownFinder.findFacts(kieSession);
        return knowns;
    }

    @Override
    public void answer(Known<?> known) {
        kieSession.insert(known);
        kieSession.fireAllRules();
    }
    
}
