package com.sctrcd.qzr.services;

import com.sctrcd.qzr.web.resources.AgendaEventMessage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * This is an {@link AgendaEventListener}, which will listen to a rules runtime
 * session for any rule activations. Every time a rule matches, this will
 * publish the name of the rule on to a Stomp message broker, using the
 * {@link SimpMessagingTemplate}.
 *
 * @author Stephen Masters
 */
public class PublishingAgendaEventListener extends DefaultAgendaEventListener {

    private static final Logger log = LoggerFactory.getLogger(PublishingAgendaEventListener.class);

    private final SimpMessagingTemplate template;

    public PublishingAgendaEventListener(SimpMessagingTemplate template) {
        this.template = template;
    }

    /**
     * Whenever a rule's LHS matches and causes the rule to activate, this
     * method will publish the name of that rule to the Stomp message broker,
     * using the {@link SimpMessagingTemplate}.
     */
    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        Rule rule = event.getMatch().getRule();

        log.debug("Publishing: {}", rule.getName());

        AgendaEventMessage msg = new AgendaEventMessage(rule.getName());

        this.template.convertAndSend("/queue/agendaevents/", msg);
    }
}
