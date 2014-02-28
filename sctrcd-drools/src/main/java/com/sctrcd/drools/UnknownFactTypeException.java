package com.sctrcd.drools;

import org.kie.api.KieBase;

/**
 * 
 * @author Stephen Masters
 */
public class UnknownFactTypeException extends Exception {

	private static final long serialVersionUID = -494523759082377899L;

    public UnknownFactTypeException() {
        super();
    }
    
    public UnknownFactTypeException(String factType, String pkg, KieBase knowledgeBase) {
        super("No [" + factType + "] fact type found in package [" + pkg + "] in knowledge base: " + DroolsUtil.knowledgeBaseDetails(knowledgeBase));
    }
    
    public UnknownFactTypeException(String message) {
        super(message);
    }
    
    public UnknownFactTypeException(Throwable cause) {
        super(cause);
    }
    
    public UnknownFactTypeException(String message, Throwable cause) {
        super(cause);
    }	
	
}
