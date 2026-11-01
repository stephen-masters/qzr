package com.sctrcd.qzr.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.sctrcd.qzr.facts.Question;
import com.sctrcd.qzr.services.HrMaxQuizService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * A test to confirm that the web application can be set up and that the beans
 * which should be autowired/injected by Spring are set up correctly. The
 * default mock web environment provides the HTTP session that the
 * session-scoped {@link HrMaxQuizService} lives in.
 *
 * @author Stephen Masters
 */
@SpringBootTest
class QzrWebConfigTest {

    @Autowired
    private HrMaxQuizService healthQuizService;

    @Test
    void shouldInjectBeans() {
        assertThat(healthQuizService).isNotNull();

        Question initialQuestion = healthQuizService.getNextQuestion();
        assertThat(initialQuestion).isNotNull();
        assertThat(initialQuestion.getKey()).isEqualTo("dateOfBirth");
    }
}
