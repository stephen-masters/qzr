package com.sctrcd.drools.spring;

import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;

public class DefaultKieContainerBean implements KieContainerBean {

    private KieServicesBean kieServices;
    private KieContainer kieContainer;
    
    public DefaultKieContainerBean(KieServicesBean kieServicesBean) {
        this.kieServices = kieServicesBean;
        this.kieContainer = this.kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
    }

    /* (non-Javadoc)
     * @see spring.KieContainedBean#getReleaseId()
     */
    @Override
    public ReleaseId getReleaseId() {
        return kieContainer.getReleaseId();
    }

    /* (non-Javadoc)
     * @see spring.KieContainedBean#verify()
     */
    @Override
    public Results verify() {
        return kieContainer.verify();
    }

    /* (non-Javadoc)
     * @see spring.KieContainedBean#updateToVersion(org.kie.api.builder.ReleaseId)
     */
    @Override
    public void updateToVersion(ReleaseId version) {
        kieContainer.updateToVersion(version);
    }

    /* (non-Javadoc)
     * @see spring.KieContainedBean#getKieBase()
     */
    @Override
    public KieBase getKieBase() {
        return kieContainer.getKieBase();
    }

    /* (non-Javadoc)
     * @see spring.KieContainedBean#getKieBase(java.lang.String)
     */
    @Override
    public KieBase getKieBase(String kBaseName) {
        return kieContainer.getKieBase(kBaseName);
    }

    /* (non-Javadoc)
     * @see spring.KieContainedBean#newKieBase(org.kie.api.KieBaseConfiguration)
     */
    @Override
    public KieBase newKieBase(KieBaseConfiguration conf) {
        return kieContainer.newKieBase(conf);
    }

    /* (non-Javadoc)
     * @see spring.KieContainedBean#newKieBase(java.lang.String, org.kie.api.KieBaseConfiguration)
     */
    @Override
    public KieBase newKieBase(String kBaseName, KieBaseConfiguration conf) {
        return kieContainer.newKieBase(kBaseName, conf);
    }

    /* (non-Javadoc)
     * @see spring.KieContainedBean#newKieSession()
     */
    @Override
    public KieSession newKieSession() {
        return kieContainer.newKieSession();
    }

    /* (non-Javadoc)
     * @see spring.KieContainedBean#newKieSession(org.kie.api.runtime.KieSessionConfiguration)
     */
    @Override
    public KieSession newKieSession(KieSessionConfiguration conf) {
        return kieContainer.newKieSession(conf);
    }

    /* (non-Javadoc)
     * @see spring.KieContainedBean#newKieSession(org.kie.api.runtime.Environment)
     */
    @Override
    public KieSession newKieSession(Environment environment) {
        return kieContainer.newKieSession(environment);
    }

    /* (non-Javadoc)
     * @see spring.KieContainedBean#newKieSession(java.lang.String)
     */
    @Override
    public KieSession newKieSession(String kSessionName) {
        return kieContainer.newKieSession(kSessionName);
    }

    /* (non-Javadoc)
     * @see spring.KieContainedBean#newKieSession(java.lang.String, org.kie.api.runtime.Environment)
     */
    @Override
    public KieSession newKieSession(String kSessionName, Environment environment) {
        return kieContainer.newKieSession(kSessionName, environment);
    }

    /* (non-Javadoc)
     * @see spring.KieContainedBean#newKieSession(java.lang.String, org.kie.api.runtime.KieSessionConfiguration)
     */
    @Override
    public KieSession newKieSession(String kSessionName,
            KieSessionConfiguration conf) {
        return kieContainer.newKieSession(kSessionName, conf);
    }

    /* (non-Javadoc)
     * @see spring.KieContainedBean#newKieSession(java.lang.String, org.kie.api.runtime.Environment, org.kie.api.runtime.KieSessionConfiguration)
     */
    @Override
    public KieSession newKieSession(String kSessionName,
            Environment environment, KieSessionConfiguration conf) {
        return kieContainer.newKieSession(kSessionName, environment, conf);
    }

    /* (non-Javadoc)
     * @see spring.KieContainedBean#newStatelessKieSession()
     */
    @Override
    public StatelessKieSession newStatelessKieSession() {
        return kieContainer.newStatelessKieSession();
    }

    /* (non-Javadoc)
     * @see spring.KieContainedBean#newStatelessKieSession(org.kie.api.runtime.KieSessionConfiguration)
     */
    @Override
    public StatelessKieSession newStatelessKieSession(
            KieSessionConfiguration conf) {
        return kieContainer.newStatelessKieSession(conf);
    }

    /* (non-Javadoc)
     * @see spring.KieContainedBean#newStatelessKieSession(java.lang.String)
     */
    @Override
    public StatelessKieSession newStatelessKieSession(String kSessionName) {
        return kieContainer.newStatelessKieSession(kSessionName);
    }

    /* (non-Javadoc)
     * @see spring.KieContainedBean#newStatelessKieSession(java.lang.String, org.kie.api.runtime.KieSessionConfiguration)
     */
    @Override
    public StatelessKieSession newStatelessKieSession(String kSessionName,
            KieSessionConfiguration conf) {
        return kieContainer.newStatelessKieSession(kSessionName, conf);
    }

    /* (non-Javadoc)
     * @see spring.KieContainedBean#getClassLoader()
     */
    @Override
    public ClassLoader getClassLoader() {
        return kieContainer.getClassLoader();
    }
    
}
