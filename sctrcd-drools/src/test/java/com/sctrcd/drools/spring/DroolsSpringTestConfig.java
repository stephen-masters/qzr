package com.sctrcd.drools.spring;

import org.kie.api.io.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.sctrcd.drools.DroolsResource;
import com.sctrcd.drools.KieBuildException;
import com.sctrcd.drools.ResourcePathType;
import com.sctrcd.drools.spring.DefaultKieContainerBean;
import com.sctrcd.drools.spring.DefaultKieServicesBean;
import com.sctrcd.drools.spring.DefaultKieSessionBean;
import com.sctrcd.drools.spring.KieContainerBean;
import com.sctrcd.drools.spring.KieServicesBean;
import com.sctrcd.drools.spring.KieSessionBean;



@Configuration
@Profile("drools")
public class DroolsSpringTestConfig {

    @Bean(name = "myKieServicesBean")
    public KieServicesBean kieServices() throws KieBuildException {
        DroolsResource[] resources = new DroolsResource[]{ 
                new DroolsResource("rules/test-rules.drl", 
                        ResourcePathType.CLASSPATH, 
                        ResourceType.DRL)};
        
        KieServicesBean bean = new DefaultKieServicesBean(resources);
        return bean;
    }
    
    @Bean(name = "myKieContainerBean")
    @Autowired
    public KieContainerBean kieContainer(KieServicesBean kieServices) {
        KieContainerBean bean = new DefaultKieContainerBean(kieServices);
        return bean;
    }
    
    @Bean(name = "myKieSessionBean")
    @Autowired
    public KieSessionBean kieSession(KieServicesBean kieServices, KieContainerBean kieContainer) {
        KieSessionBean bean = new DefaultKieSessionBean(kieServices, kieContainer);
        return bean;
    }
    
}
