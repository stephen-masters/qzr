package com.sctrcd.qzr.web.controllers;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.joda.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Exercises every endpoint of the HR max quiz API that the page uses, plus
 * the rest of the API, through MockMvc. The quiz lives in a session-scoped
 * service, so each test keeps one {@link MockHttpSession} across its requests,
 * as a browser keeps its session cookie.
 */
@SpringBootTest
@AutoConfigureMockMvc
class HrMaxQuizControllerTest {

    private static final String API = "/api/quizzes/health";

    @Autowired
    private MockMvc mvc;

    private final MockHttpSession session = new MockHttpSession();

    private MockHttpServletRequestBuilder inSession(MockHttpServletRequestBuilder request) {
        return request.session(session);
    }

    private MockHttpServletRequestBuilder answer(String key, String value) {
        return inSession(put(API + "/questions/{key}/answer", key)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"key\":\"" + key + "\",\"value\":\"" + value + "\"}"));
    }

    @Test
    void shouldServeTheQuizHomeWithLinks() throws Exception {
        mvc.perform(inSession(get(API + "/")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("hrmax"))
                .andExpect(jsonPath("$._links.self.href").value(endsWith(API + "/")))
                .andExpect(jsonPath("$._links.nextQuestion.href").value(endsWith(API + "/questions/next")))
                .andExpect(jsonPath("$._links.answers.href").value(endsWith(API + "/answers")))
                .andExpect(jsonPath("$._links.knowns.href").value(endsWith(API + "/knowns")))
                .andExpect(jsonPath("$._links.hrmax.href").value(endsWith(API + "/results/hrmax")));
    }

    @Test
    void shouldStartByAskingForDateOfBirth() throws Exception {
        mvc.perform(inSession(get(API + "/questions")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].key").value("dateOfBirth"))
                .andExpect(jsonPath("$[0].text").value("What is your date of birth? (yyyy-MM-dd)"))
                .andExpect(jsonPath("$[0].answerType").value("LOCAL_DATE"))
                .andExpect(jsonPath("$[0].answer").doesNotExist())
                // A plain list is not a HAL document, so its items carry Spring HATEOAS's
                // default "links" array rather than "_links".
                .andExpect(jsonPath("$[0].links[0].rel").value("self"))
                .andExpect(jsonPath("$[0].links[0].href").value(endsWith(API + "/questions/dateOfBirth")));

        mvc.perform(inSession(get(API + "/questions/next")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("dateOfBirth"))
                .andExpect(jsonPath("$._links.self.href").value(endsWith(API + "/questions/dateOfBirth")))
                .andExpect(jsonPath("$._links.answer.href").value(endsWith(API + "/questions/dateOfBirth/answer")));

        mvc.perform(inSession(get(API + "/questions/dateOfBirth")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("dateOfBirth"));
    }

    @Test
    void shouldReportWhatIsNotThereYet() throws Exception {
        mvc.perform(inSession(get(API + "/results/hrmax")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("HRMAX_NOT_FOUND"));
        mvc.perform(inSession(get(API + "/questions/gender")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
        mvc.perform(inSession(get(API + "/questions/dateOfBirth/answer")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NO_ANSWER"));
        mvc.perform(inSession(get(API + "/knowns"))).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
        mvc.perform(inSession(get(API + "/answers"))).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldRejectAnEmptyAnswer() throws Exception {
        mvc.perform(answer("dateOfBirth", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Not a valid answer."));
    }

    @Test
    void shouldCalculateHrMaxFromAnswers() throws Exception {
        String dob = new LocalDate().minusYears(40).toString();

        mvc.perform(answer("dateOfBirth", dob))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("dateOfBirth"))
                .andExpect(jsonPath("$.value").value(dob))
                .andExpect(jsonPath("$.when").isString())
                .andExpect(jsonPath("$._links.self.href").value(endsWith(API + "/questions/dateOfBirth/answer")))
                .andExpect(jsonPath("$._links.question.href").value(endsWith(API + "/questions/dateOfBirth")));

        mvc.perform(inSession(get(API + "/questions/dateOfBirth/answer")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(dob));

        mvc.perform(inSession(get(API + "/questions/next")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("gender"))
                .andExpect(jsonPath("$.answerType").value("STRING"))
                .andExpect(jsonPath("$.options[*].key", containsInAnyOrder("M", "F")))
                .andExpect(jsonPath("$.options[*].text", containsInAnyOrder("Male", "Female")));

        mvc.perform(inSession(get(API + "/results/hrmax")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(180))
                .andExpect(jsonPath("$.algorithm").value("Best guess"));

        mvc.perform(answer("gender", "M")).andExpect(status().isOk());

        mvc.perform(inSession(get(API + "/results/hrmax")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(182))
                .andExpect(jsonPath("$.algorithm").value("Male-adjusted"));

        mvc.perform(inSession(get(API + "/questions/next")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NO_MORE_QUESTIONS"));

        // Answered questions are still listed, with their answers attached.
        mvc.perform(inSession(get(API + "/questions")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].key", containsInAnyOrder("dateOfBirth", "gender")))
                .andExpect(jsonPath("$[?(@.key == 'gender')].answer.value").value("M"))
                .andExpect(jsonPath("$[?(@.key == 'dateOfBirth')].answer.value").value(dob));

        mvc.perform(inSession(get(API + "/answers")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].key", containsInAnyOrder("dateOfBirth", "gender")));

        // Joda dates are written as ISO-8601 strings, not as objects.
        mvc.perform(inSession(get(API + "/knowns")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].key", containsInAnyOrder("dateOfBirth", "age", "gender", "hrMax")))
                .andExpect(jsonPath("$[?(@.key == 'dateOfBirth')].value").value(dob))
                .andExpect(jsonPath("$[?(@.key == 'age')].value").value(40))
                .andExpect(jsonPath("$[?(@.key == 'hrMax')].value").value(182))
                .andExpect(jsonPath("$[0].when").isString());
    }

    @Test
    void shouldSkipAQuestion() throws Exception {
        mvc.perform(answer("dateOfBirth", new LocalDate().minusYears(40).toString()))
                .andExpect(status().isOk());

        mvc.perform(inSession(post(API + "/questions/gender/skip"))).andExpect(status().isNoContent());

        mvc.perform(inSession(get(API + "/questions/next"))).andExpect(status().isNotFound());
        mvc.perform(inSession(get(API + "/questions/gender")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").exists())
                .andExpect(jsonPath("$.answer.value").value(nullValue()));
        mvc.perform(inSession(get(API + "/results/hrmax")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithm").value("Best guess"));
    }

    @Test
    void shouldRetractAnAnswer() throws Exception {
        mvc.perform(answer("dateOfBirth", "1980-06-06")).andExpect(status().isOk());
        mvc.perform(inSession(get(API + "/results/hrmax"))).andExpect(status().isOk());

        mvc.perform(inSession(delete(API + "/questions/dateOfBirth/answer"))).andExpect(status().isOk());

        mvc.perform(inSession(get(API + "/questions/next")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("dateOfBirth"));
        mvc.perform(inSession(get(API + "/results/hrmax"))).andExpect(status().isNotFound());
        mvc.perform(inSession(get(API + "/answers"))).andExpect(jsonPath("$", hasSize(0)));
        mvc.perform(inSession(get(API + "/knowns"))).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldKeepEachSessionsQuizSeparate() throws Exception {
        mvc.perform(answer("dateOfBirth", "1980-06-06")).andExpect(status().isOk());

        mvc.perform(get(API + "/questions/next").session(new MockHttpSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("dateOfBirth"));
        mvc.perform(inSession(get(API + "/questions/next")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("gender"));
    }
}
