package com.sctrcd.qzr.rules;

import java.util.List;

import org.drools.core.io.impl.UrlResource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.sctrcd.drools.DroolsResource;
import com.sctrcd.drools.KieBuildException;
import com.sctrcd.drools.ResourcePathType;

/**
 * This is a configuration class, which will set up Spring beans for a Drools
 * {@link KieServices} and a {@link KieContainer}.
 * 
 * @author Stephen Masters
 */
@Configuration
@Profile("drools")
public class HealthQuizKieConfig {

    private static Logger log = LoggerFactory.getLogger(HealthQuizKieConfig.class);
    
    /**
     * Spring Bean providing a {@link KieServices} instance which among other
     * things,encapsulates a {@link KieFileSystem} where rules are stored.
     * Generally unless you're writing code to modify the rules at runtime, you
     * will not need to reference this in any components. But it's provided as a
     * bean, just in case.
     * 
     * @return A {@link KieServices}.
     * @throws KieBuildException
     *             If there are compilation errors in the rules.
     */
    @Bean(name = "healthQuizKieServices")
    public KieServices kieServices() throws KieBuildException {
        
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        
        DroolsResource[] resources = new DroolsResource[]{ 
                new DroolsResource("rules/health-quiz.drl", 
                        ResourcePathType.CLASSPATH, 
                        ResourceType.DRL)};
        
        createAndBuildKieServices(kieServices, kfs, resources);
        
        return kieServices;
    }
    
    /**
     * The {@link KieContainer} provides the means to get hold of a
     * {@link KieSession}. As such, this is what you will usually be
     * injected into components.
     * 
     * @param kieServices
     *            The {@link KieServices} bean.
     * @return A {@link KieContainer}.
     */
    @Bean(name = "healthQuizKieContainer")
    public KieContainer kieContainer(
            @Qualifier("healthQuizKieServices") KieServices kieServices) {
        
        KieContainer bean = kieServices.newKieContainer(
                kieServices.getRepository().getDefaultReleaseId());
        
        return bean;
    }
    
    /**
     * Creates a new {@link KieServices} using a collection of resources. It
     * does this by creating a new virtual file system and adding the resources
     * to it.
     * 
     * @param resources
     *            An array of {@link DroolsResource} indicating where the
     *            various resources should be loaded from. These could be
     *            classpath, file or URL resources.
     * @return A new {@link KieContainer}.
     * @throws KieBuildException 
     */
    private void createAndBuildKieServices(KieServices kieServices, KieFileSystem kfs, DroolsResource[] resources) throws KieBuildException {

        log.info("Building KieServices with resources: " + resources);
        
        for (DroolsResource resource : resources) {
            log.info("Resource: " + resource.getType() + ", path type="
                    + resource.getPathType() + ", path=" + resource.getPath());
            switch (resource.getPathType()) {
            case CLASSPATH:
                kfs.write(ResourceFactory.newClassPathResource(resource.getPath()));
                break;
            case FILE:
                kfs.write(ResourceFactory.newFileResource(resource.getPath()));
                break;
            case URL:
                UrlResource urlResource = (UrlResource) ResourceFactory
                        .newUrlResource(resource.getPath());
                
                if (resource.getUsername() != null) {
                    log.info("Setting authentication for: " + resource.getUsername());
                    urlResource.setBasicAuthentication("enabled");
                    urlResource.setUsername(resource.getUsername());
                    urlResource.setPassword(resource.getPassword());
                }
                
                kfs.write(urlResource);
                
                break;
            default:
                throw new IllegalArgumentException(
                        "Unable to build this resource path type.");
            }
        }
        
        KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
        
        if (kieBuilder.getResults().hasMessages(Level.ERROR)) {
            List<Message> errors = kieBuilder.getResults().getMessages(Level.ERROR);
            StringBuilder sb = new StringBuilder("Errors:");
            for (Message msg : errors) {
                sb.append("\n  " + prettyBuildMessage(msg));
            }
            throw new KieBuildException(sb.toString());
        }
        
        log.info("KieServices built: " + toString());
    }
    
    private static String prettyBuildMessage(Message msg) {
        return "Message: {"
            + "id="+ msg.getId()
            + ", level=" + msg.getLevel()
            + ", path=" + msg.getPath()
            + ", line=" + msg.getLine()
            + ", column=" + msg.getColumn()
            + ", text=\"" + msg.getText() + "\""
            + "}";
    }
    
}
