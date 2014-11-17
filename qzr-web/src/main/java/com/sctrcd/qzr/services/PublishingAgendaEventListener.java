package com.sctrcd.qzr.services;

import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.sctrcd.qzr.web.resources.AgendaEventMessage;

@Component
public class PublishingAgendaEventListener extends DefaultAgendaEventListener {
    
    private final SimpMessagingTemplate template;
    
    @Autowired
    public PublishingAgendaEventListener(SimpMessagingTemplate template) {
        this.template = template;
    }
    
    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        Rule rule = event.getMatch().getRule();
        
        System.out.println("Publishing: " + rule.getName());

        AgendaEventMessage msg = new AgendaEventMessage(rule.getName());

        this.template.convertAndSend("/topic/agendaevents/", msg);
    }

}
