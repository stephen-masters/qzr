package com.sctrcd.qzr.web;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.sctrcd.qzr.Qzr;
import com.sctrcd.qzr.facts.Question;
import com.sctrcd.qzr.services.HrMaxQuizService;

/**
 * A test to confirm that the web application can be set up and that the beans
 * which should be autowired/injected by Spring are set up correctly.
 * 
 * @author Stephen Masters
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Qzr.class)
@WebAppConfiguration
@ActiveProfiles({ "scratch" })
public class QzrWebConfigTest {

    @Autowired
    private HrMaxQuizService healthQuizService;

    @Test
    public void shouldInjectBeans() {
        assertNotNull(healthQuizService);
        
        Question initialQuestion = healthQuizService.getNextQuestion();
        assertNotNull(initialQuestion);
    }

}
