package com.sctrcd.qzr.rules;

import org.kie.api.io.ResourceType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.sctrcd.drools.DroolsResource;
import com.sctrcd.drools.KieBuildException;
import com.sctrcd.drools.ResourcePathType;

import com.sctrcd.drools.spring.DefaultKieContainerBean;
import com.sctrcd.drools.spring.DefaultKieServicesBean;
import com.sctrcd.drools.spring.KieContainerBean;
import com.sctrcd.drools.spring.KieServicesBean;

@Configuration
@Profile("drools")
public class HealthQuizKieConfig {

    @Bean(name = "healthQuizKieServices")
    public KieServicesBean kieServices() throws KieBuildException {
        DroolsResource[] resources = new DroolsResource[]{ 
                new DroolsResource("rules/health-quiz.drl", 
                        ResourcePathType.CLASSPATH, 
                        ResourceType.DRL)};
        
        KieServicesBean bean = new DefaultKieServicesBean(resources);
        return bean;
    }
    
    @Bean(name = "healthQuizKieContainer")
    public KieContainerBean kieContainer(KieServicesBean kieServices) {
        KieContainerBean bean = new DefaultKieContainerBean(kieServices);
        return bean;
    }
    
}
