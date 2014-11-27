package com.sctrcd.qzr.rules;

import static org.junit.Assert.*;

import java.util.Collection;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sctrcd.beans.BeanPropertyFilter;
import com.sctrcd.drools.DroolsUtil;
import com.sctrcd.drools.FactFinder;
import com.sctrcd.drools.KieBuildException;
import com.sctrcd.drools.monitoring.TrackingAgendaEventListener;
import com.sctrcd.drools.monitoring.TrackingRuleRuntimeEventListener;
import com.sctrcd.qzr.facts.HrMax;
import com.sctrcd.qzr.facts.Known;
import com.sctrcd.qzr.facts.Question;

/**
 * This runs tests against the knowledge base defined in the {@link HealthQuizKieConfig}.
 * 
 * @author Stephen Masters
 */
public class HrmaxRulesTest {

    private static Logger log = LoggerFactory.getLogger(HrmaxRulesTest.class);

    private KieServices kieServices;

    private KieContainer kieContainer;
    
    private KieSession kieSession;

    private TrackingAgendaEventListener agendaEventListener;
    private TrackingRuleRuntimeEventListener workingMemoryEventListener;
    
    private FactFinder<Question> valueQuestionFinder = new FactFinder<>(Question.class);
    private FactFinder<Known<?>> knownFinder = new FactFinder<>(Known.class);
    private FactFinder<HrMax> hrMaxFinder = new FactFinder<>(HrMax.class);

    /**
     * Each test should start with a fresh session. We also attach fresh event
     * listeners to that session, which we can use to track what rules have
     * fired and what facts have been inserted/modified/retracted.
     * 
     * @throws KieBuildException
     *             If there's a problem instantiating a new session.
     */
    @Before
    public void initialize() throws KieBuildException {
        if (kieSession != null) {
            kieSession.dispose();
        }

        this.kieServices = KieServices.Factory.get();
        this.kieContainer = kieServices.getKieClasspathContainer(); 
        this.kieSession = kieContainer.newKieSession("HrmaxSession");
        
        agendaEventListener = new TrackingAgendaEventListener();
        workingMemoryEventListener = new TrackingRuleRuntimeEventListener();

        kieSession.addEventListener(agendaEventListener);
        kieSession.addEventListener(workingMemoryEventListener);
    }

    /**
     * If this passes, then the Spring components were autowired.
     */
    @Test
    public void shouldConfigureDroolsComponents() {
        assertNotNull(kieSession);
        
        for (ObjectInsertedEvent ev : workingMemoryEventListener.getInsertions()) {
            System.out.println(DroolsUtil.objectDetails(ev.getObject()));
        }
    }

    @Test
    public void shouldAskDateOfBirthIfUnknown() {
        kieSession.fireAllRules();

        Collection<Question> dateOfBirthQuestions = valueQuestionFinder.findFacts(kieSession, 
                new BeanPropertyFilter("key", "dateOfBirth"));
        
        Collection<Question> allQuestions = valueQuestionFinder.findFacts(kieSession);
        System.out.println("ALl questions: " + allQuestions);

        assertEquals("Should ask date of birth if not known.", 1, dateOfBirthQuestions.size());
        
        LocalDate dob = new LocalDate(1980, 06, 06); 
        
        @SuppressWarnings("unused")
        FactHandle dobFact = kieSession.insert(new Known<>("dateOfBirth", dob));
        
        kieSession.fireAllRules();

        dateOfBirthQuestions = valueQuestionFinder.findFacts(kieSession, 
                new BeanPropertyFilter("key", "dateOfBirth"));

        assertEquals("Should not ask date of birth if already known.", 0, dateOfBirthQuestions.size());
    }
    
    @Test
    public void shouldInferAge() {
        LocalDate today = new LocalDate(kieSession.getSessionClock().getCurrentTime());
        LocalDate dob = today.minusYears(40);
        
        @SuppressWarnings("unused")
        FactHandle ageFact = kieSession.insert(new Known<>("dateOfBirth", dob));
        
        kieSession.fireAllRules();
        kieSession.getSessionClock().getCurrentTime();
        
        assertTrue("Should infer age if date of birth is known.", hasKnownValue("age", 40));
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
        assertTrue("Should calculate default HR max if date of birth is known, but not gender.", hasHrMax(180));
        
        FactHandle male = kieSession.insert(new Known<>("gender", "M"));
        kieSession.fireAllRules();
        
        // Male-adjusted = 214 - (0.8 * age)
        assertTrue("Should calculate male-adjusted HR max if date of birth is known and is male.", hasHrMax(182));
        
        kieSession.delete(male);
        kieSession.insert(new Known<>("gender", "F"));
        kieSession.fireAllRules();
        
        // Female-adjusted = 209 - (0.9 * age)
        assertTrue("Should calculate female-adjusted HR max if date of birth is known and is female.", hasHrMax(173));
    }

    private boolean hasKnownValue(String key, Object expected) {
        Collection<Known<?>> facts = knownFinder.findFacts(kieSession, new BeanPropertyFilter("key", key));
        assertEquals(1, facts.size());
        Known<?> known = facts.iterator().next();
        log.debug("Found " + known);
        return expected.equals(known.getValue());
    }
    
    private boolean hasHrMax(Integer expected) {
        Collection<HrMax> facts = hrMaxFinder.findFacts(kieSession);
        assertEquals(1, facts.size());
        HrMax hrMax = facts.iterator().next();
        log.debug("Found " + hrMax);
        return expected.equals(hrMax.getValue());
    }
    
}
