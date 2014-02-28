package com.sctrcd.drools.spring;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.drools.core.SessionConfiguration;
import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.WorkingMemoryEventListener;
import org.kie.api.runtime.Calendars;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.rule.Agenda;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.SessionEntryPoint;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.api.time.SessionClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultKieSessionBean implements KieSessionBean {

    private static Logger log = LoggerFactory.getLogger(DefaultKieSessionBean.class);

    private KieSession kieSession;
    
    public DefaultKieSessionBean(KieServicesBean kieServices, KieContainerBean kieContainer) {
        this(kieServices, kieContainer, null);
    }
    
    public DefaultKieSessionBean(KieServicesBean kieServices, KieContainerBean kieContainer, Properties droolsProperties) {
        log.info("Initialising session...");

        KieSessionConfiguration conf;
        if (droolsProperties == null) {
            conf = SessionConfiguration.getDefaultInstance();
        } else {
            conf = new SessionConfiguration(droolsProperties);
        }
        this.kieSession = kieContainer.newKieSession(conf);
    }

    public void addEventListener(WorkingMemoryEventListener listener) {
        kieSession.addEventListener(listener);
    }

    public void addEventListener(ProcessEventListener listener) {
        kieSession.addEventListener(listener);
    }

    public ProcessInstance startProcess(String processId) {
        return kieSession.startProcess(processId);
    }

    public void removeEventListener(WorkingMemoryEventListener listener) {
        kieSession.removeEventListener(listener);
    }

    public int fireAllRules() {
        return kieSession.fireAllRules();
    }

    public void removeEventListener(ProcessEventListener listener) {
        kieSession.removeEventListener(listener);
    }

    public <T extends SessionClock> T getSessionClock() {
        return kieSession.getSessionClock();
    }

    public int fireAllRules(int max) {
        return kieSession.fireAllRules(max);
    }

    public Collection<WorkingMemoryEventListener> getWorkingMemoryEventListeners() {
        return kieSession.getWorkingMemoryEventListeners();
    }

    public Collection<ProcessEventListener> getProcessEventListeners() {
        return kieSession.getProcessEventListeners();
    }

    public void setGlobal(String identifier, Object value) {
        kieSession.setGlobal(identifier, value);
    }

    public void halt() {
        kieSession.halt();
    }

    public ProcessInstance startProcess(String processId,
            Map<String, Object> parameters) {
        return kieSession.startProcess(processId, parameters);
    }

    public void addEventListener(AgendaEventListener listener) {
        kieSession.addEventListener(listener);
    }

    public Object getGlobal(String identifier) {
        return kieSession.getGlobal(identifier);
    }

    public Globals getGlobals() {
        return kieSession.getGlobals();
    }

    public Calendars getCalendars() {
        return kieSession.getCalendars();
    }

    public void removeEventListener(AgendaEventListener listener) {
        kieSession.removeEventListener(listener);
    }

    public Environment getEnvironment() {
        return kieSession.getEnvironment();
    }

    public KieBase getKieBase() {
        return kieSession.getKieBase();
    }

    public int fireAllRules(AgendaFilter agendaFilter) {
        return kieSession.fireAllRules(agendaFilter);
    }

    public void registerChannel(String name, Channel channel) {
        kieSession.registerChannel(name, channel);
    }

    public Collection<AgendaEventListener> getAgendaEventListeners() {
        return kieSession.getAgendaEventListeners();
    }

    public String getEntryPointId() {
        return kieSession.getEntryPointId();
    }

    public void unregisterChannel(String name) {
        kieSession.unregisterChannel(name);
    }

    public Map<String, Channel> getChannels() {
        return kieSession.getChannels();
    }

    public Agenda getAgenda() {
        return kieSession.getAgenda();
    }

    public int fireAllRules(AgendaFilter agendaFilter, int max) {
        return kieSession.fireAllRules(agendaFilter, max);
    }

    public FactHandle insert(Object object) {
        return kieSession.insert(object);
    }

    public KieSessionConfiguration getSessionConfiguration() {
        return kieSession.getSessionConfiguration();
    }

    public SessionEntryPoint getEntryPoint(String name) {
        return kieSession.getEntryPoint(name);
    }

    public ProcessInstance createProcessInstance(String processId,
            Map<String, Object> parameters) {
        return kieSession.createProcessInstance(processId, parameters);
    }

    @Deprecated
    public void retract(FactHandle handle) {
        kieSession.retract(handle);
    }

    public Collection<? extends SessionEntryPoint> getEntryPoints() {
        return kieSession.getEntryPoints();
    }

    public void fireUntilHalt() {
        kieSession.fireUntilHalt();
    }

    public <T> T execute(Command<T> command) {
        return kieSession.execute(command);
    }

    public void delete(FactHandle handle) {
        kieSession.delete(handle);
    }

    public QueryResults getQueryResults(String query, Object... arguments) {
        return kieSession.getQueryResults(query, arguments);
    }

    public void update(FactHandle handle, Object object) {
        kieSession.update(handle, object);
    }

    public void fireUntilHalt(AgendaFilter agendaFilter) {
        kieSession.fireUntilHalt(agendaFilter);
    }

    public FactHandle getFactHandle(Object object) {
        return kieSession.getFactHandle(object);
    }

    public LiveQuery openLiveQuery(String query, Object[] arguments,
            ViewChangedEventListener listener) {
        return kieSession.openLiveQuery(query, arguments, listener);
    }

    public ProcessInstance startProcessInstance(long processInstanceId) {
        return kieSession.startProcessInstance(processInstanceId);
    }

    public Object getObject(FactHandle factHandle) {
        return kieSession.getObject(factHandle);
    }

    public int getId() {
        return kieSession.getId();
    }

    public void signalEvent(String type, Object event) {
        kieSession.signalEvent(type, event);
    }

    public void dispose() {
        kieSession.dispose();
    }

    public Collection<? extends Object> getObjects() {
        return kieSession.getObjects();
    }

    public void destroy() {
        kieSession.destroy();
    }

    public void signalEvent(String type, Object event, long processInstanceId) {
        kieSession.signalEvent(type, event, processInstanceId);
    }

    public Collection<? extends Object> getObjects(ObjectFilter filter) {
        return kieSession.getObjects(filter);
    }

    public <T extends FactHandle> Collection<T> getFactHandles() {
        return kieSession.getFactHandles();
    }

    public <T extends FactHandle> Collection<T> getFactHandles(
            ObjectFilter filter) {
        return kieSession.getFactHandles(filter);
    }

    public Collection<ProcessInstance> getProcessInstances() {
        return kieSession.getProcessInstances();
    }

    public long getFactCount() {
        return kieSession.getFactCount();
    }

    public ProcessInstance getProcessInstance(long processInstanceId) {
        return kieSession.getProcessInstance(processInstanceId);
    }

    public ProcessInstance getProcessInstance(long processInstanceId,
            boolean readonly) {
        return kieSession.getProcessInstance(processInstanceId, readonly);
    }

    public void abortProcessInstance(long processInstanceId) {
        kieSession.abortProcessInstance(processInstanceId);
    }

    public WorkItemManager getWorkItemManager() {
        return kieSession.getWorkItemManager();
    }
    
}
