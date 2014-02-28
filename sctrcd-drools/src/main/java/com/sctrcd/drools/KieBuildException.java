package com.sctrcd.drools;

/**
 * 
 * @author Stephen Masters
 */
public class KieBuildException extends Exception {

	/** Generated serialVersionUID. */
	private static final long serialVersionUID = 8045260631214957224L;

	public KieBuildException() {
        super();
    }
    
    public KieBuildException(String message) {
        super(message);
    }
    
    public KieBuildException(Throwable cause) {
        super(cause);
    }
    
    public KieBuildException(String message, Throwable cause) {
        super(cause);
    }	
	
    public KieBuildException(Object o, Throwable cause) {
        super("Error marshalling object: " + DroolsUtil.objectDetails(o), cause);
    }

}
