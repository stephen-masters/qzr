package com.sctrcd.qzr.rules;

import static org.junit.Assert.*;

import java.util.Collection;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.sctrcd.beans.BeanPropertyFilter;
import com.sctrcd.drools.FactFinder;
import com.sctrcd.drools.KieBuildException;
import com.sctrcd.drools.monitoring.TrackingAgendaEventListener;
import com.sctrcd.drools.monitoring.TrackingWorkingMemoryEventListener;
import com.sctrcd.drools.spring.DefaultKieSessionBean;
import com.sctrcd.drools.spring.KieContainerBean;
import com.sctrcd.drools.spring.KieServicesBean;
import com.sctrcd.drools.spring.KieSessionBean;
import com.sctrcd.qzr.facts.Known;
import com.sctrcd.qzr.facts.Question;
import com.sctrcd.qzr.facts.ValueQuestion;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HealthQuizKieConfig.class }, loader = AnnotationConfigContextLoader.class)
@ActiveProfiles({ "drools" })
public class HealthQuizRulesTest {

    private static Logger log = LoggerFactory.getLogger(HealthQuizRulesTest.class);

    @Autowired
    @Qualifier("healthQuizKieServices")
    private KieServicesBean kieServices;

    @Autowired
    @Qualifier("healthQuizKieContainer")
    private KieContainerBean kieContainer;

    private KieSessionBean kieSession;

    private TrackingAgendaEventListener agendaEventListener;
    private TrackingWorkingMemoryEventListener workingMemoryEventListener;
    
    private FactFinder<Question> valueQuestionFinder = new FactFinder<>(ValueQuestion.class);
    private FactFinder<Known<?>> knownFinder = new FactFinder<>(Known.class);

    @Before
    public void initialize() throws KieBuildException {
        if (kieSession != null) {
            kieSession.dispose();
        }
        kieSession = new DefaultKieSessionBean(kieServices, kieContainer);
        
        agendaEventListener = new TrackingAgendaEventListener();
        workingMemoryEventListener = new TrackingWorkingMemoryEventListener();

        kieSession.addEventListener(agendaEventListener);
        kieSession.addEventListener(workingMemoryEventListener);
    }

    /**
     * If this passes, then the Spring components were autowired.
     */
    @Test
    public void shouldConfigureDroolsComponents() {
        assertNotNull(kieServices);
        assertNotNull(kieContainer);
        assertNotNull(kieSession);
    }

    @Test
    public void shouldAskDateOfBirthIfUnknown() {
        kieSession.fireAllRules();

        Collection<Question> questions = valueQuestionFinder.findFacts(kieSession, 
                new BeanPropertyFilter("key", "dateOfBirth"));

        assertEquals("Should ask date of birth if not known.", 1, questions.size());
        
        LocalDate dob = new LocalDate(1980, 06, 06); 
        FactHandle dobFact = kieSession.insert(new Known<>("dateOfBirth", dob));
        kieSession.fireAllRules();

        questions = valueQuestionFinder.findFacts(kieSession, 
                new BeanPropertyFilter("key", "dateOfBirth"));

        assertEquals("Should not ask date of birth if already known.", 0, questions.size());
    }
    
    @Test
    public void shouldInferAge() {
        LocalDate today = new LocalDate(kieSession.getSessionClock().getCurrentTime());
        LocalDate dob = today.minusYears(40);
        
        FactHandle ageFact = kieSession.insert(new Known<>("dateOfBirth", dob));
        kieSession.fireAllRules();
        kieSession.getSessionClock().getCurrentTime();
        
        assertTrue("Should infer age if date fo birth is known.", hasKnownValue("age", 40));
    }
    
    /**
     * There is a default max HR calculation, but there are also male and female
     * optimised calculations, which are better. If the gender is known, then
     * the gender-specific calculation should be used.
     */
    @Test
    public void shouldInferHrMax() {
        kieSession.insert(new Known<>("age", 40));
        kieSession.fireAllRules();
        
        // Default = 220 - age
        assertTrue("Should calculate default HR max if date of birth is known, but not gender.", hasKnownValue("hrMax", 180));
        
        FactHandle male = kieSession.insert(new Known<>("gender", "M"));
        kieSession.fireAllRules();
        
        // Male-adjusted = 214 - (0.8 * age)
        assertTrue("Should calculate male-adjusted HR max if date of birth is known and is male.", hasKnownValue("hrMax", 182));
        
        kieSession.delete(male);
        kieSession.insert(new Known<>("gender", "F"));
        kieSession.fireAllRules();
        
        // Female-adjusted = 209 - (0.9 * age)
        assertTrue("Should calculate female-adjusted HR max if date of birth is known and is female.", hasKnownValue("hrMax", 173));
    }

    private boolean hasKnownValue(String key, Object expected) {
        Collection<Known<?>> facts = knownFinder.findFacts(kieSession, new BeanPropertyFilter("key", key));
        assertEquals(1, facts.size());
        Known known = facts.iterator().next();
        log.debug("Found " + known);
        return expected.equals(known.getValue());
    }
    
}
