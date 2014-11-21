package com.sctrcd.drools.monitoring;

import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A listener that will track all rule firings in a session.
 * 
 * @author Stephen Masters
 */
public class LoggingAgendaEventListener extends DefaultAgendaEventListener {

    private static Logger log = LoggerFactory.getLogger(LoggingAgendaEventListener.class);

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        Rule rule = event.getMatch().getRule();

        StringBuilder sb = new StringBuilder("Rule fired: " + rule.getName());

        log.debug(sb.toString());
    }

    @Override
    public String toString() {
        return LoggingAgendaEventListener.class.getSimpleName();
    }

}
