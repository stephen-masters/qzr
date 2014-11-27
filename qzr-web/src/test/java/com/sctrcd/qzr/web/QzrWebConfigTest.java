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
