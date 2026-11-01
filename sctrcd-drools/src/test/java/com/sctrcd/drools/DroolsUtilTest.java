package com.sctrcd.drools;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sctrcd.beans.BeanPropertyFilter;
import com.sctrcd.drools.monitoring.TrackingAgendaEventListener;
import com.sctrcd.drools.monitoring.TrackingRuleRuntimeEventListener;
import com.sctrcd.drools.testfacts.Customer;
import com.sctrcd.drools.testfacts.Product;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * Builds a knowledge base from a classpath DRL with {@link DroolsUtil}, then checks that the
 * {@link FactFinder} and the tracking listeners report what the rules did.
 */
class DroolsUtilTest {

    private KieSession session;
    private TrackingAgendaEventListener agendaListener;
    private TrackingRuleRuntimeEventListener ruleRuntimeListener;

    @BeforeEach
    void createSession() throws KieBuildException {
        KieContainer container = DroolsUtil.createKieContainer(new DroolsResource[] {
            new DroolsResource("rules/test-rules.drl", ResourcePathType.CLASSPATH, ResourceType.DRL)
        });
        session = container.newKieSession();
        agendaListener = new TrackingAgendaEventListener();
        ruleRuntimeListener = new TrackingRuleRuntimeEventListener();
        session.addEventListener(agendaListener);
        session.addEventListener(ruleRuntimeListener);
    }

    @AfterEach
    void disposeSession() {
        session.dispose();
    }

    @Test
    void shouldFireRulesFromClasspathResource() {
        Customer customer = new Customer("Ada");
        session.insert(customer);
        session.insert(new Product("Widget", 0));
        session.insert(new Product("Gadget", 3));

        int fired = session.fireAllRules();

        assertThat(fired).isEqualTo(2);
        assertThat(customer.isGreeted()).isTrue();
        assertThat(agendaListener.isRuleFired("Greet customer")).isTrue();
        assertThat(agendaListener.allRulesFired(new String[] {"Greet customer", "Restock product"}))
                .isTrue();
        assertThat(agendaListener.getActivationList()).hasSize(2);
        assertThat(ruleRuntimeListener.getInsertions()).hasSize(3);
        assertThat(ruleRuntimeListener.getUpdates()).hasSize(2);
    }

    @Test
    void shouldFindAndDeleteFactsByProperty() {
        session.insert(new Product("Widget", 0));
        session.insert(new Product("Gadget", 3));
        session.insert(new Customer("Ada"));
        session.fireAllRules();

        FactFinder<Product> products = new FactFinder<>(Product.class);
        assertThat(products.findFacts(session)).hasSize(2);

        List<Product> widgets = products.findFacts(session, new BeanPropertyFilter("name", "Widget"));
        assertThat(widgets).singleElement().extracting(Product::getCount).isEqualTo(10);

        products.deleteFacts(session, new BeanPropertyFilter("name", "Widget"));

        assertThat(products.findFacts(session))
                .singleElement()
                .extracting(Product::getName)
                .isEqualTo("Gadget");
        assertThat(ruleRuntimeListener.getRetractions()).hasSize(1);
        assertThat(new FactFinder<Customer>(Customer.class).findFacts(session)).hasSize(1);
    }

    @Test
    void shouldReportCompilationErrors() {
        assertThatThrownBy(() -> DroolsUtil.createKieContainer(new DroolsResource[] {
                    new DroolsResource("rules/broken-rules.drl", ResourcePathType.CLASSPATH, ResourceType.DRL)
                }))
                .isInstanceOf(KieBuildException.class)
                .hasMessageContaining("Errors:");
    }
}
