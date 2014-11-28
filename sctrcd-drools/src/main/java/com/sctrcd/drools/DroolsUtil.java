package com.sctrcd.drools;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.drools.core.io.impl.UrlResource;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.io.ResourceFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Stephen Masters
 */
public class DroolsUtil {

    private static Logger log = LoggerFactory.getLogger(DroolsUtil.class);
    
    /**
     * Private to prevent instantiation. Everything here should be called statically.
     */
    private DroolsUtil() {}
    
    /**
     * Creates a new {@link KieContainer} using a collection of resources. It
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
    public static KieContainer createKieContainer(DroolsResource[] resources) throws KieBuildException {
        KieServices kieServices = createAndBuildKieServices(resources);        
        KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        return kieContainer;
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
     * @return A new {@link KieServices}.
     * @throws KieBuildException I there are errors whilst building the {@link KieServices}.
     */
    public static KieServices createAndBuildKieServices(DroolsResource[] resources) throws KieBuildException {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        
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
        
        // The KieBuilder contains a collection of messages, which is built up
        // as it does its job. If any of these have a level of 'ERROR', then the
        // rules did not compile correctly. Therefore we log such messages and 
        // throw an exception to indicate failure.
        if (kieBuilder.getResults().hasMessages(Level.ERROR)) {
            List<Message> errors = kieBuilder.getResults().getMessages(Level.ERROR);
            StringBuilder sb = new StringBuilder("Errors:");
            for (Message msg : errors) {
                sb.append("\n  " + prettyBuildMessage(msg));
            }
            throw new KieBuildException(sb.toString());
        }
        
        return kieServices;
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

    /**
     * Return a string containing the packages used to build the knowledge base.
     */
    public static String knowledgeBaseDetails(KieBase kbase) {
        if (kbase == null) {
            return "Knowledge Base is null.";
        } else {
            StringBuilder sb = new StringBuilder(
                    "Knowledge base built from the following packages:");
            Collection<KiePackage> packages = kbase
                    .getKiePackages();
            for (KiePackage kp : packages) {
                sb.append("\n    Package: [" + kp.getName() + "]");
                for (Rule rule : kp.getRules()) {
                    sb.append("\n        Rule: [" + rule.getName() + "]");
                }
            }
            return sb.toString();
        }
    }

    /**
     * Uses Apache Commons {@link BeanUtils} to get the public properties of an
     * object (the 'get' and 'is' methods) and build them up into a readable
     * description of that object.
     * 
     * @param o
     *            The object.
     * @return A {@link String} describing the object.
     */
    public static String objectDetails(Object o) {
        StringBuilder sb = new StringBuilder(o.getClass().getSimpleName());

        try {
            Map<String, String> objectProperties = BeanUtils.describe(o);
            for (String k : objectProperties.keySet()) {
                sb.append(", " + k + "=\"" + objectProperties.get(k) + "\"");
            }
        } catch (IllegalAccessException e) {
            return "IllegalAccessException attempting to parse object.";
        } catch (InvocationTargetException e) {
            return "InvocationTargetException attempting to parse object.";
        } catch (NoSuchMethodException e) {
            return "NoSuchMethodException attempting to parse object.";
        }

        return sb.toString();
    }

    /**
     * Iterates through the facts currently in working memory, and logs their details.
     * 
     * @param session The session to search for facts.
     */
    public static void printFacts(KieSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n************************************************************");
        sb.append("\nThe following facts are currently in the system...");
        for (Object fact : session.getObjects()) {
            sb.append("\n\nFact: " + DroolsUtil.objectDetails(fact));
        }
        sb.append("\n************************************************************\n");
        log.info(sb.toString());
    }
    
    /**
     * 
     * @return A String detailing the packages and rules in this knowledge base.
     */
    public static String kbaseDetails(KieBase kbase) {
        StringBuilder sb = new StringBuilder();
        for (KiePackage p : kbase.getKiePackages()) {
            sb.append("\n  Package : " + p.getName());
            for (Rule r : p.getRules()) {
                sb.append("\n    Rule: " + r.getName());
            }
        }
        return "Knowledge base built with packages: " + sb.toString();
    }
    
    public static Object getObject(KieSession ksession, FactHandle handle) {
        if (handle == null) {
            return null;
        } else {
            return ksession.getObject(handle);
        }
    }

    /**
     * Find all handles to facts in working memory matching an
     * {@link ObjectFilter}. For example, to find all facts of a class called
     * "MyObject":
     * 
     * <pre>
     * getFactHandles(new ObjectFilter() {
     *     public boolean accept(Object object) {
     *         return object.getClass().getSimpleName()
     *                 .equals(MyObject.class.getSimpleName());
     *     }
     * });
     * </pre>
     * 
     * @param filter
     *            The {@link ObjectFilter}.
     * @return A collection of facts matching the filter.
     */
    public static Collection<FactHandle> getFactHandles(KieSession ksession, ObjectFilter filter) {
        return ksession.getFactHandles(filter);
    }
    
    /**
     * Gets handles to all facts in the {@link KieSession} and then retracts
     * them all.
     * 
     * @param ksession
     *            The {@link KieSession} we wish to clear down.
     */
    public static void retractAll(KieSession ksession) {
        log.debug("Retracting all facts...");
        retract(ksession, ksession.getFactHandles());
    }
    
    /**
     * Retract all fact handles from working memory, which match an
     * {@link ObjectFilter}. For example, to retract all facts of a 
     * class called "MyObject":
     * 
     * <pre>
     * retractAll(new ObjectFilter() {
     *     public boolean accept(Object object) {
     *         return object.getClass().getSimpleName()
     *                 .equals(MyObject.class.getSimpleName());
     *     }
     * });
     * </pre>
     * 
     * @param filter
     *            The {@link ObjectFilter}.
     */
    public static void retractAll(KieSession ksession, ObjectFilter filter) {
        log.info("Retracting all facts matching filter...");
        for (FactHandle handle : getFactHandles(ksession, filter)) {
            retract(ksession, handle);
        }
    }

    /**
     * The insert method accepts a list of arguments and returns a list of fact
     * handles. Therefore this is retract method which can accept such a list.
     * 
     * @param handles
     *            The fact handles you wish to retract.
     */
    public static void retract(KieSession ksession, Collection<FactHandle> handles) {
        for (FactHandle handle : handles) {
            retract(ksession, handle);
        }
    }

    /**
     * Retract a specific fact from the {@link KieSession}.
     * 
     * @param ksession
     *            The {@link KieSession} containing the fact.
     * @param handle
     *            A handle to the fact we wish to retract.
     */
    public static void retract(KieSession ksession, FactHandle handle) {
        ksession.delete(handle);
    }

}
