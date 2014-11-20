package com.sctrcd.qzr.web.config;

import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.sctrcd.qzr.Qzr;
import com.sctrcd.qzr.facts.Known;
import com.sctrcd.qzr.facts.Question;
import com.sctrcd.qzr.services.HrMaxQuizServiceImpl;
import com.sctrcd.qzr.services.QuizService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Qzr.class)
@WebAppConfiguration
@ActiveProfiles({ "scratch" })
public class QzrWebConfigTest {

    @Autowired
    private QuizService healthQuizService;

    @Test
    public void shouldInjectBeans() {
        assertNotNull(healthQuizService);
        healthQuizService.getNextQuestion();
    }

    @Test
    public void shouldRemoveQuestionWhenAnswered() {
        Question question = healthQuizService.getNextQuestion();
        Collection<Known<?>> knowns = healthQuizService.getKnowns();

        // questions.contains(o)
    }

}
