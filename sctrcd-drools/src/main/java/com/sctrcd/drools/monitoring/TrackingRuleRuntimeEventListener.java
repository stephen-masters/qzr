package com.sctrcd.drools.monitoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEvent;
import org.drools.core.event.DefaultRuleRuntimeEventListener;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sctrcd.beans.BeanMatcher;
import com.sctrcd.drools.DroolsUtil;

/**
 * When validation rules fire, they should insert a TradeValidationAnnotation
 * into the working memory. This class listens for these events, and adds them
 * to a list so that a client can query all the alerts raised for a request.
 * <p>
 * You should probably avoid using this outside testing, as the contained lists
 * of events will grow and grow.
 * </p>
 * 
 * @author Stephen Masters
 */
public class TrackingRuleRuntimeEventListener
            extends DefaultRuleRuntimeEventListener {

    private static Logger log = LoggerFactory.getLogger(TrackingRuleRuntimeEventListener.class);

    private List<RuleRuntimeEvent> allEvents = new ArrayList<>();
    private List<ObjectInsertedEvent> insertions = new ArrayList<>();
    private List<ObjectDeletedEvent> deletions = new ArrayList<>();
    private List<ObjectUpdatedEvent> updates = new ArrayList<>();

    private List<Map<String, String>> factChanges = new ArrayList<>();

    private FactHandle handleFilter;
    private Class<?> classFilter;

    /**
     * Void constructor sets the listener to record all working memory events
     * with no filtering.
     */
    public TrackingRuleRuntimeEventListener() {
        this.handleFilter = null;
    }
    
    /**
     * Constructor which sets up an event filter. The listener will only record
     * events when the event {@link FactHandle} matches the constructor argument.
     * 
     * @param handle
     *            The {@link FactHandle} to filter on.
     */
    public TrackingRuleRuntimeEventListener(FactHandle handle) {
        this.handleFilter = handle;
    }
    
    public TrackingRuleRuntimeEventListener(Class<?> classFilter) {
        this.handleFilter = null;
        this.classFilter = classFilter;
    }

    @Override
    public void objectInserted(final ObjectInsertedEvent event) {
        if ((handleFilter == null  && classFilter == null)
                || event.getFactHandle() == handleFilter
                || event.getObject().getClass().equals(classFilter)) {
            insertions.add(event);
            allEvents.add(event);
            log.trace("Insertion: " + DroolsUtil.objectDetails(event.getObject()));
        }
    }

    @Override
    public void objectDeleted(final ObjectDeletedEvent event) {
        if ((handleFilter == null  && classFilter == null) 
                || event.getFactHandle() == handleFilter
                || event.getOldObject().getClass().equals(classFilter)) {
            deletions.add(event);
            allEvents.add(event);
            log.trace("Retraction: " + DroolsUtil.objectDetails(event.getOldObject()));
        }
    }

    @Override
    public void objectUpdated(final ObjectUpdatedEvent event) {
        if ((handleFilter == null  && classFilter == null) 
                || event.getFactHandle() == handleFilter
                || event.getObject().getClass().equals(classFilter)) {
            updates.add(event);
            allEvents.add(event);

            Object fact = event.getObject();
            try {
                factChanges.add(BeanUtils.describe(fact));
            } catch (Exception e) {
                log.error("Unable to get object details for tracking: " + DroolsUtil.objectDetails(fact), e);
            }
            log.trace("Update: " + DroolsUtil.objectDetails(event.getObject()));
        }
    }

    public Object findInsertedFact(String factType, String[] filters) {
        BeanMatcher matcher = new BeanMatcher();
        
        for (ObjectInsertedEvent event : this.insertions) {
            Object fact = event.getObject();

            if (factType.equals(fact.getClass().getSimpleName())) {
                if (matcher.matches(fact, filters)) {
                    return fact;
                }
            }
        }
        return null;
    }
    
    public List<RuleRuntimeEvent> getAllEvents() {
        return allEvents;
    }

    public List<ObjectInsertedEvent> getInsertions() {
        return insertions;
    }

    public List<ObjectDeletedEvent> getRetractions() {
        return deletions;
    }

    public List<ObjectUpdatedEvent> getUpdates() {
        return updates;
    }

    public List<Map<String, String>> getFactChanges() {
        return factChanges;
    }

    public String getPrintableSummary() {
        return "TrackingWorkingMemoryEventListener: " +
                "insertions=[" + insertions.size() + "], " +
                "retractions=[" + deletions.size() + "], " +
                "updates=[" + updates.size() + "]";
    }

    public String getPrintableDetail() {
        StringBuilder report = new StringBuilder(
                "TrackingWorkingMemoryEventListener: " + "insertions=["
                        + insertions.size() + "], " + "retractions=["
                        + deletions.size() + "], " + "updates=["
                        + updates.size() + "]");

        for (ObjectInsertedEvent event : insertions) {
            report.append("\n" + DroolsUtil.objectDetails(event.getObject()));
        }
        for (ObjectDeletedEvent event : deletions) {
            report.append("\n" + DroolsUtil.objectDetails(event.getOldObject()));
        }
        for (ObjectUpdatedEvent event : updates) {
            report.append("\n" + DroolsUtil.objectDetails(event.getObject()));
        }

        return report.toString();
    }

}
