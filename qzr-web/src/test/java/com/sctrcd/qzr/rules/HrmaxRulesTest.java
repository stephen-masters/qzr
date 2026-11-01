package com.sctrcd.qzr.rules;

import static org.assertj.core.api.Assertions.assertThat;

import com.sctrcd.beans.BeanPropertyFilter;
import com.sctrcd.drools.DroolsUtil;
import com.sctrcd.drools.FactFinder;
import com.sctrcd.drools.monitoring.TrackingAgendaEventListener;
import com.sctrcd.drools.monitoring.TrackingRuleRuntimeEventListener;
import com.sctrcd.qzr.facts.Answer;
import com.sctrcd.qzr.facts.AnswerType;
import com.sctrcd.qzr.facts.HrMax;
import com.sctrcd.qzr.facts.Known;
import com.sctrcd.qzr.facts.Option;
import com.sctrcd.qzr.facts.Question;
import java.util.Collection;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This runs tests against the HrmaxKbase knowledge base defined in
 * <code>META-INF/kmodule.xml</code>.
 *
 * @author Stephen Masters
 */
class HrmaxRulesTest {

    private static final Logger log = LoggerFactory.getLogger(HrmaxRulesTest.class);

    private KieSession kieSession;

    private TrackingAgendaEventListener agendaEventListener;
    private TrackingRuleRuntimeEventListener workingMemoryEventListener;

    private final FactFinder<Question> valueQuestionFinder = new FactFinder<>(Question.class);
    private final FactFinder<Known<?>> knownFinder = new FactFinder<>(Known.class);
    private final FactFinder<Answer> answerFinder = new FactFinder<>(Answer.class);
    private final FactFinder<HrMax> hrMaxFinder = new FactFinder<>(HrMax.class);

    /**
     * Each test should start with a fresh session. We also attach fresh event
     * listeners to that session, which we can use to track what rules have
     * fired and what facts have been inserted/modified/retracted.
     */
    @BeforeEach
    void initialize() {
        KieContainer kieContainer = KieServices.Factory.get().getKieClasspathContainer();

        this.kieSession = kieContainer.newKieSession("HrmaxSession");

        agendaEventListener = new TrackingAgendaEventListener();
        workingMemoryEventListener = new TrackingRuleRuntimeEventListener();

        kieSession.addEventListener(agendaEventListener);
        kieSession.addEventListener(workingMemoryEventListener);
    }

    @AfterEach
    void dispose() {
        kieSession.dispose();
    }

    /**
     * If this passes, then the Drools {@link KieSession} was started without
     * errors.
     */
    @Test
    void shouldConfigureDroolsComponents() {
        assertThat(kieSession).isNotNull();
        assertThat(kieSession.getKieBase().getRule("com.sctrcd.qzr.hrmax", "Ask dateOfBirth"))
                .isNotNull();

        for (ObjectInsertedEvent ev : workingMemoryEventListener.getInsertions()) {
            log.debug(DroolsUtil.objectDetails(ev.getObject()));
        }
    }

    /**
     * The first thing that we need to know to determine is the date of birth of
     * a person. Confirm that if the date of birth is not known, the rules will
     * insert a question asking for it.
     */
    @Test
    void shouldAskDateOfBirthIfUnknown() {
        kieSession.fireAllRules();

        Collection<Question> dateOfBirthQuestions =
                valueQuestionFinder.findFacts(kieSession, new BeanPropertyFilter("key", "dateOfBirth"));

        assertThat(dateOfBirthQuestions)
                .as("Should ask date of birth if not known.")
                .hasSize(1);
        assertThat(dateOfBirthQuestions.iterator().next().getAnswerType()).isEqualTo(AnswerType.LOCAL_DATE);

        // Now that the question has been asked, we provide an answer.
        // So the rules should not ask again.

        LocalDate dob = new LocalDate(1980, 6, 6);

        kieSession.insert(new Known<>("dateOfBirth", dob));

        kieSession.fireAllRules();

        dateOfBirthQuestions = valueQuestionFinder.findFacts(kieSession, new BeanPropertyFilter("key", "dateOfBirth"));

        assertThat(dateOfBirthQuestions)
                .as("Should not ask date of birth if already known.")
                .isEmpty();
    }

    /**
     * If a date of birth is provided, then the rules should infer the person's
     * age. The inferred age is used in heart rate calculations later on.
     */
    @Test
    void shouldInferAge() {
        LocalDate today = new LocalDate(kieSession.getSessionClock().getCurrentTime());
        LocalDate dob = today.minusYears(40);

        kieSession.insert(new Known<>("dateOfBirth", dob));

        kieSession.fireAllRules();

        assertThat(hasKnownValue("age", 40))
                .as("Should infer age if date of birth is known.")
                .isTrue();
    }

    /**
     * There is a default max HR calculation, but there are also male and female
     * optimised calculations, which are better. If the gender is known, then
     * the gender-specific calculation should be used.
     */
    @Test
    void shouldInferHrMax() {
        kieSession.insert(new Known<>("age", 40));
        kieSession.fireAllRules();

        // Default = 220 - age
        assertThat(hasHrMax(180))
                .as("Should calculate default HR max if date of birth is known, but not gender.")
                .isTrue();

        FactHandle male = kieSession.insert(new Known<>("gender", "M"));
        kieSession.fireAllRules();

        // Male-adjusted = 214 - (0.8 * age)
        assertThat(hasHrMax(182))
                .as("Should calculate male-adjusted HR max if date of birth is known and is male.")
                .isTrue();

        kieSession.delete(male);
        kieSession.insert(new Known<>("gender", "F"));
        kieSession.fireAllRules();

        // Female-adjusted = 209 - (0.9 * age)
        assertThat(hasHrMax(173))
                .as("Should calculate female-adjusted HR max if date of birth is known and is female.")
                .isTrue();
    }

    /**
     * Answering the date of birth question binds the answer to the question,
     * learns the date of birth, and moves on to asking for the gender.
     */
    @Test
    void shouldLearnDateOfBirthFromAnswerAndAskGender() {
        kieSession.fireAllRules();
        Question dobQuestion = singleQuestion("dateOfBirth");

        Answer answer = new Answer("dateOfBirth", "1980-06-06");
        kieSession.insert(answer);
        kieSession.fireAllRules();

        assertThat(answer.getQuestion()).isSameAs(dobQuestion);
        assertThat(dobQuestion.getAnswer()).isSameAs(answer);
        assertThat(knownValue("dateOfBirth")).isEqualTo(new LocalDate(1980, 6, 6));
        assertThat(valueQuestionFinder.findFacts(kieSession, new BeanPropertyFilter("key", "dateOfBirth")))
                .isEmpty();

        Question gender = singleQuestion("gender");
        assertThat(gender.getAnswerType()).isEqualTo(AnswerType.STRING);
        assertThat(gender.getOptions())
                .extracting(Option::getKey, Option::getText)
                .containsExactlyInAnyOrder(
                        org.assertj.core.groups.Tuple.tuple("M", "Male"),
                        org.assertj.core.groups.Tuple.tuple("F", "Female"));
        assertThat(agendaEventListener.allRulesFired(
                        new String[] {"Ask dateOfBirth", "Bind question to answer", "Learn date of birth", "Ask gender"
                        }))
                .isTrue();
    }

    /**
     * An answer that cannot be parsed as a date is deleted, and the question
     * is asked again with no answer attached to it.
     */
    @Test
    void shouldDiscardUnparseableDateOfBirth() {
        kieSession.fireAllRules();

        kieSession.insert(new Answer("dateOfBirth", "not a date"));
        kieSession.fireAllRules();

        assertThat(answerFinder.findFacts(kieSession)).isEmpty();
        assertThat(knownFinder.findFacts(kieSession)).isEmpty();
        assertThat(singleQuestion("dateOfBirth").getAnswer()).isNull();
        assertThat(agendaEventListener.isRuleFired("Unbind answer from question"))
                .isTrue();
    }

    /** When a question is answered twice, only the more recent answer is kept. */
    @Test
    void shouldRetractOlderAnswers() {
        DateTime now = DateTime.now(DateTimeZone.UTC);
        kieSession.insert(new Answer("gender", "M", now.minusMinutes(1)));
        kieSession.insert(new Answer("gender", "F", now));
        kieSession.fireAllRules();

        List<Answer> answers = answerFinder.findFacts(kieSession);
        assertThat(answers).singleElement().extracting(Answer::getValue).isEqualTo("F");
        assertThat(knownValue("gender")).isEqualTo("F");
    }

    /**
     * The whole quiz, driven by answers as the web application drives it: a
     * date of birth 40 years ago and a gender give the gender-adjusted max HR.
     */
    @Test
    void shouldCalculateHrMaxFromAnswers() {
        kieSession.fireAllRules();
        String dob = new LocalDate().minusYears(40).toString();

        kieSession.insert(new Answer("dateOfBirth", dob));
        kieSession.fireAllRules();
        assertThat(hrMax().getAlgorithm()).isEqualTo("Best guess");
        assertThat(hrMax().getValue()).isEqualTo(180);

        kieSession.insert(new Answer("gender", "F"));
        kieSession.fireAllRules();

        assertThat(hrMax().getValue()).isEqualTo(173);
        assertThat(hrMax().getAlgorithm()).isEqualTo("Female-adjusted");
        assertThat(knownValue("hrMax")).isEqualTo(173);
        assertThat(valueQuestionFinder.findFacts(kieSession)).isEmpty();
    }

    /**
     * Skipping the gender question is an answer with no value. The quiz has no
     * more questions, and the max HR stays on the best guess.
     */
    @Test
    void shouldKeepBestGuessWhenGenderIsSkipped() {
        kieSession.insert(
                new Answer("dateOfBirth", new LocalDate().minusYears(40).toString()));
        kieSession.fireAllRules();

        kieSession.insert(new Answer("gender", null));
        kieSession.fireAllRules();

        assertThat(valueQuestionFinder.findFacts(kieSession)).isEmpty();
        assertThat(hrMax().getAlgorithm()).isEqualTo("Best guess");
        assertThat(hrMax().getValue()).isEqualTo(180);
    }

    /**
     * Everything inferred from an answer is logically inserted, so deleting the
     * answer takes back what was learnt and asks the question again.
     */
    @Test
    void shouldForgetInferencesWhenAnswerIsDeleted() {
        kieSession.fireAllRules();
        FactHandle dob = kieSession.insert(new Answer("dateOfBirth", "1980-06-06"));
        kieSession.fireAllRules();
        assertThat(hrMaxFinder.findFacts(kieSession)).hasSize(1);

        kieSession.delete(dob);
        kieSession.fireAllRules();

        assertThat(knownFinder.findFacts(kieSession)).isEmpty();
        assertThat(hrMaxFinder.findFacts(kieSession)).isEmpty();
        assertThat(singleQuestion("dateOfBirth").getAnswer()).isNull();
        assertThat(valueQuestionFinder.findFacts(kieSession, new BeanPropertyFilter("key", "gender")))
                .isEmpty();
    }

    private Question singleQuestion(String key) {
        List<Question> questions = valueQuestionFinder.findFacts(kieSession, new BeanPropertyFilter("key", key));
        assertThat(questions).hasSize(1);
        return questions.get(0);
    }

    private Object knownValue(String key) {
        Collection<Known<?>> facts = knownFinder.findFacts(kieSession, new BeanPropertyFilter("key", key));
        assertThat(facts).hasSize(1);
        return facts.iterator().next().getValue();
    }

    private HrMax hrMax() {
        Collection<HrMax> facts = hrMaxFinder.findFacts(kieSession);
        assertThat(facts).hasSize(1);
        return facts.iterator().next();
    }

    /**
     * Searches for {@link Known} facts in the {@link KieSession}. If one of
     * them is equal to the 'expected' {@link Known} provided, then this method
     * returns true. Otherwise it returns false.
     */
    private boolean hasKnownValue(String key, Object expected) {
        Collection<Known<?>> facts = knownFinder.findFacts(kieSession, new BeanPropertyFilter("key", key));
        assertThat(facts).hasSize(1);
        Known<?> known = facts.iterator().next();
        log.debug("Found " + known);
        return expected.equals(known.getValue());
    }

    private boolean hasHrMax(Integer expected) {
        Collection<HrMax> facts = hrMaxFinder.findFacts(kieSession);
        assertThat(facts).hasSize(1);
        HrMax hrMax = facts.iterator().next();
        log.debug("Found " + hrMax);
        return expected.equals(hrMax.getValue());
    }
}
