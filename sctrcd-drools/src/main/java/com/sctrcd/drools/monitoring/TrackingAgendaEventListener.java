package com.sctrcd.drools.monitoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class TrackingAgendaEventListener extends DefaultAgendaEventListener {

    private static Logger log = LoggerFactory.getLogger(TrackingAgendaEventListener.class);

    private List<Activation> activationList = new ArrayList<Activation>();

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        Rule rule = event.getMatch().getRule();

        String ruleName = rule.getName();
        Map<String, Object> ruleMetaDataMap = rule.getMetaData();

        addActivation(new Activation(ruleName));
        StringBuilder sb = new StringBuilder("Rule fired: " + ruleName);

        if (ruleMetaDataMap.size() > 0) {
            sb.append("\n  With [" + ruleMetaDataMap.size() + "] meta-data:");
            for (String key : ruleMetaDataMap.keySet()) {
                sb.append("\n    key=" + key + ", value="
                        + ruleMetaDataMap.get(key));
            }
        }

        log.debug(sb.toString());
    }
    
    private void addActivation(Activation activation) {
        this.activationList.add(activation);
    }

    public boolean isRuleFired(String ruleName) {
        return activationList.contains(ruleName);
    }

    public void reset() {
        activationList.clear();
    }

    public final List<Activation> getActivationList() {
        return activationList;
    }
    
    public String activationsToString() {
        if (activationList.size() == 0) {
            return "No activations occurred.";
        } else {
            StringBuilder sb = new StringBuilder("Activations: ");
            for (Activation activation : activationList) {
                sb.append("\n  rule: ").append(activation.getRuleName());
            }
            return sb.toString();
        }
    }
    
    /**
     * Search for an activation by rule name. Note that when using decision
     * tables, the rule name is generated as <code>Row N Rule_Name</code>. This
     * means that we can't just search for exact matches. This method will
     * therefore return true if an activation ends with the ruleName argument.
     * 
     * @param ruleName
     *            The name of the rule we're looking for.
     */
    private boolean ruleFired(String ruleName) {
        for (Activation activation : this.activationList) {
            if (activation.getRuleName().toUpperCase()
                    .endsWith(ruleName.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Did all of the rules in the list provided get fired? This is a utility
     * method designed primarily to help with testing. For instance, you can
     * write a test which inserts some facts and fires rules. Using this method,
     * you can assert that a specific list of expected rules fired.
     * 
     * @param ruleName
     *            The list of rule names we're looking for.
     */
    public boolean allRulesFired(String[] expectedRuleNames) {
        for (String ruleName : expectedRuleNames) {
            if (!ruleFired(ruleName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a <code>String</code> showing the rule names that were expected
     * to fire, and whether or not they fired, ready to be logged.
     * 
     * @param ruleName
     *            The list of rule names we're looking for.
     */
    public String prettyRulesFired(String[] ruleNames) {
        StringBuilder sb = new StringBuilder();
        for (String ruleName : ruleNames) {
            sb.append("\n    " + ruleName + " : "
                    + (ruleFired(ruleName) ? "Y" : "N"));
        }
        return sb.toString();
    }

}
