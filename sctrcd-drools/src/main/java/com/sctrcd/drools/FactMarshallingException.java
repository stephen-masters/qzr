package com.sctrcd.drools;

/**
 * 
 * @author Stephen Masters
 */
public class FactMarshallingException extends Exception {

	/** Generated serialVersionUID. */
	private static final long serialVersionUID = 8045260631214957224L;

	public FactMarshallingException() {
        super();
    }
    
    public FactMarshallingException(String message) {
        super(message);
    }
    
    public FactMarshallingException(Throwable cause) {
        super(cause);
    }
    
    public FactMarshallingException(String message, Throwable cause) {
        super(cause);
    }	
	
    public FactMarshallingException(Object o, Throwable cause) {
        super("Error marshalling object: " + DroolsUtil.objectDetails(o), cause);
    }

}
