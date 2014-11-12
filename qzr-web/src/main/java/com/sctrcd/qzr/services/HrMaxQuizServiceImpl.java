package com.sctrcd.qzr.services;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
import com.sctrcd.qzr.facts.Question;

@Service
@Scope(value="session", proxyMode=ScopedProxyMode.INTERFACES)
public class HrMaxQuizServiceImpl implements QuizService {

    private static Logger log = LoggerFactory.getLogger(HrMaxQuizServiceImpl.class);
            
    public KieSession kieSession;
    
    private TrackingAgendaEventListener agendaEventListener;
    private TrackingWorkingMemoryEventListener workingMemoryEventListener;
    
    private FactFinder<Question> questionFinder = new FactFinder<>(Question.class);
    private FactFinder<Known<?>> knownFinder = new FactFinder<>(Known.class);
    private FactFinder<Answer> answerFinder = new FactFinder<>(Answer.class);
    private FactFinder<HrMax> hrMaxFinder = new FactFinder<>(HrMax.class);
    
    @Autowired
    public HrMaxQuizServiceImpl(
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
    
    @Override
    public Question getNextQuestion() {
        List<Question> questions = questionFinder.findFacts(kieSession);
        
        if (questions.size() == 0) return null;
        if (questions.size() > 1) {
            log.error("Multiple (" + questions.size() + ") questions found.");
        }
        Question nextQuestion = questions.get(0);
        return nextQuestion;
    }
    
    @Override
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
    public Answer answer(Answer answer) {
        kieSession.insert(answer);
        kieSession.fireAllRules();
        return answer;
    }
    
    @Override
    public void retractAnswer(String key) {
        answerFinder.deleteFacts(kieSession, new BeanPropertyFilter("key", key));
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
    
}
