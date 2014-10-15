package com.sctrcd.qzr.services;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

import com.sctrcd.qzr.config.QzrServicesConfig;
import com.sctrcd.qzr.facts.Known;
import com.sctrcd.qzr.facts.Question;
import com.sctrcd.qzr.services.HealthQuizService;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ "drools" })
@WebAppConfiguration
@ContextConfiguration(classes = { QzrServicesConfig.class }, loader = AnnotationConfigWebContextLoader.class)
public class HealthQuizServiceTest {

    @Autowired
    private HealthQuizService healthQuizService;
    
    @Test
    public void shouldInjectBeans() {
        assertNotNull(healthQuizService);
        healthQuizService.getQuestions();
    }
    
    @Test
    public void shouldRemoveQuestionWhenAnswered() {
        Collection<Question> questions = healthQuizService.getQuestions();
        Collection<Known<?>> knowns = healthQuizService.getKnowns();
        
        //questions.contains(o)
    }
    
}
