package com.sctrcd.drools.monitoring;

/**
 * An activation is a record of when a rule has fired in a knowledge session.
 * Key is the name of the rule that fired, but there may also be metadata.
 * 
 * @author Stephen Masters
 */
public class Activation {

	/**
	 * The name of the rule which was activated.
	 */
	private String ruleName;

	/**
	 * Default no-args constructor.
	 */
	public Activation() {
	}

	/**
	 * Simple constructor for just noting a rule name for an activation.
	 * 
	 * @param ruleName
	 *            The name of the rule which was activated.
	 */
	public Activation(String ruleName) {
		this.ruleName = ruleName;
	}

	/**
	 * @return The name of the rule which was activated.
	 */
	public String getRuleName() {
		return ruleName;
	}

	/**
	 * @param ruleName The name of the rule which was activated.
	 */
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Activation[ruleName=" + ruleName);
		sb.append("]");
		return sb.toString();
	}

}
