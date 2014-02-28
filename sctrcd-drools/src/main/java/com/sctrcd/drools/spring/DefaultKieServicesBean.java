package com.sctrcd.drools.spring;

import java.io.File;
import java.util.List;

import org.drools.core.io.impl.UrlResource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.command.KieCommands;
import org.kie.api.io.KieResources;
import org.kie.api.io.ResourceType;
import org.kie.api.logger.KieLoggers;
import org.kie.api.marshalling.KieMarshallers;
import org.kie.api.persistence.jpa.KieStoreServices;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sctrcd.drools.DroolsResource;
import com.sctrcd.drools.KieBuildException;
import com.sctrcd.drools.ResourcePathType;

public class DefaultKieServicesBean implements KieServicesBean {

    private static Logger log = LoggerFactory.getLogger(DefaultKieServicesBean.class);
    
    private DroolsResource[] resources;
    
    private KieServices kieServices;
    private KieFileSystem kfs; 
    
    public DefaultKieServicesBean(DroolsResource[] resources) throws KieBuildException {
        log.info("Initialising KnowledgeEnvironment with resources: " + this.resources);
        this.resources = resources;
        createAndBuildKieServices(resources);
    }

    /**
     * Initialises the {@link KieServices} by downloading the package from the
     * Guvnor REST interface, at the location defined in the URL.
     * 
     * @param url The URL of the package via the Guvnor REST API.
     * @throws KieBuildException 
     */
    public DefaultKieServicesBean(String url) throws KieBuildException {
        log.info("Initialising KnowledgeEnvironment with resources: " + this.resources);
        this.resources = new DroolsResource[] { 
                new DroolsResource(url,
                        ResourcePathType.URL, 
                        ResourceType.PKG
            )};
        createAndBuildKieServices(resources);
    }

    /**
     * Initialises the knowledge environment by downloading the package from the
     * Guvnor REST interface, at the location defined in the URL.
     * 
     * @param url The URL of the package via the Guvnor REST API.
     * @param username The Guvnor user name.
     * @param password The Guvnor password.
     * @throws KieBuildException 
     */
    public DefaultKieServicesBean(String url, String username, String password) throws KieBuildException {
        this.resources = new DroolsResource[] { 
                new DroolsResource(url, 
                        ResourcePathType.URL, 
                        ResourceType.PKG, 
                        username, 
                        password
        )};
        createAndBuildKieServices(resources);
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
    private void createAndBuildKieServices(DroolsResource[] resources) throws KieBuildException {
        this.kieServices = KieServices.Factory.get();
        this.kfs = newKieFileSystem();
        
        for (DroolsResource resource : resources) {
            log.info("Resource: " + resource.getType() + ", path type="
                    + resource.getPathType() + ", path=" + resource.getPath());
            switch (resource.getPathType()) {
            case CLASSPATH:
                this.kfs.write(ResourceFactory.newClassPathResource(resource.getPath()));
                break;
            case FILE:
                this.kfs.write(ResourceFactory.newFileResource(resource.getPath()));
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
                
                this.kfs.write(urlResource);
                
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

    
    /* (non-Javadoc)
     * @see spring.KieServicesBean#getResources()
     */
    @Override
    public KieResources getResources() {
        return kieServices.getResources();
    }

    /* (non-Javadoc)
     * @see spring.KieServicesBean#getRepository()
     */
    @Override
    public KieRepository getRepository() {
        return kieServices.getRepository();
    }

    /* (non-Javadoc)
     * @see spring.KieServicesBean#getCommands()
     */
    @Override
    public KieCommands getCommands() {
        return kieServices.getCommands();
    }

    /* (non-Javadoc)
     * @see spring.KieServicesBean#getMarshallers()
     */
    @Override
    public KieMarshallers getMarshallers() {
        return kieServices.getMarshallers();
    }

    /* (non-Javadoc)
     * @see spring.KieServicesBean#getLoggers()
     */
    @Override
    public KieLoggers getLoggers() {
        return kieServices.getLoggers();
    }

    /* (non-Javadoc)
     * @see spring.KieServicesBean#getStoreServices()
     */
    @Override
    public KieStoreServices getStoreServices() {
        return kieServices.getStoreServices();
    }

    /* (non-Javadoc)
     * @see spring.KieServicesBean#getKieClasspathContainer()
     */
    @Override
    public KieContainer getKieClasspathContainer() {
        return kieServices.getKieClasspathContainer();
    }

    /* (non-Javadoc)
     * @see spring.KieServicesBean#newKieContainer(org.kie.api.builder.ReleaseId)
     */
    @Override
    public KieContainer newKieContainer(ReleaseId releaseId) {
        return kieServices.newKieContainer(releaseId);
    }

    /* (non-Javadoc)
     * @see spring.KieServicesBean#newKieScanner(org.kie.api.runtime.KieContainer)
     */
    @Override
    public KieScanner newKieScanner(KieContainer kieContainer) {
        return kieServices.newKieScanner(kieContainer);
    }

    /* (non-Javadoc)
     * @see spring.KieServicesBean#newKieBuilder(java.io.File)
     */
    @Override
    public KieBuilder newKieBuilder(File rootFolder) {
        return kieServices.newKieBuilder(rootFolder);
    }

    /* (non-Javadoc)
     * @see spring.KieServicesBean#newKieBuilder(org.kie.api.builder.KieFileSystem)
     */
    @Override
    public KieBuilder newKieBuilder(KieFileSystem kieFileSystem) {
        return kieServices.newKieBuilder(kieFileSystem);
    }

    /* (non-Javadoc)
     * @see spring.KieServicesBean#newReleaseId(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ReleaseId newReleaseId(String groupId, String artifactId,
            String version) {
        return kieServices.newReleaseId(groupId, artifactId, version);
    }

    /* (non-Javadoc)
     * @see spring.KieServicesBean#newKieFileSystem()
     */
    @Override
    public KieFileSystem newKieFileSystem() {
        return kieServices.newKieFileSystem();
    }

    /* (non-Javadoc)
     * @see spring.KieServicesBean#newKieModuleModel()
     */
    @Override
    public KieModuleModel newKieModuleModel() {
        return kieServices.newKieModuleModel();
    }
    
}
