package com.sctrcd.drools;

import org.kie.api.io.ResourceType;

/**
 * A simple representation of a Drools resource.
 * 
 * @author Stephen Masters
 */
public class DroolsResource {

	private String path;
	private ResourcePathType pathType;
	private ResourceType type;
	private String username = null;
	private String password = null;

	/**
	 * 
	 * @param path
	 *            The path to this resource.
	 * @param pathType
	 *            The type of path (FILE, URL, etc).
	 * @param type
	 *            The type of resource (DRL, Binary package, DSL, etc)
	 */
	public DroolsResource(String path, ResourcePathType pathType,
			ResourceType type) {
		this.path = path;
		this.pathType = pathType;
		this.type = type;
	}

	/**
	 * Constructor for when the resource is secured. i.e. When the resource is a
	 * Guvnor package being accessed via the REST API, and Guvnor requires a
	 * user name and password to connect.
	 * 
	 * @param path
	 *            The path to this resource.
	 * @param pathType
	 *            The type of path (FILE, URL, etc).
	 * @param type
	 *            The type of resource (DRL, Binary package, DSL, etc)
	 * @param username
	 *            The user name for connecting to the resource.
	 * @param password
	 *            The password for connecting to the resource.
	 */
	public DroolsResource(String path, ResourcePathType pathType,
			ResourceType type, String username, String password) {
		this.path = path;
		this.pathType = pathType;
		this.type = type;
		this.username = username;
		this.password = password;
	}

	/**
	 * @return The path to this resource.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            The path to this resource.
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return The type of path (FILE, URL, etc).
	 */
	public ResourcePathType getPathType() {
		return pathType;
	}

	/**
	 * @param pathType
	 *            The type of path (FILE, URL, etc).
	 */
	public void setPathType(ResourcePathType pathType) {
		this.pathType = pathType;
	}

	/**
	 * @return The type of resource (DRL, Binary package, DSL, etc)
	 */
	public ResourceType getType() {
		return type;
	}

	/**
	 * @param type
	 *            The type of resource (DRL, Binary package, DSL, etc)
	 */
	public void setType(ResourceType type) {
		this.type = type;
	}

	/**
	 * @return The user name for connecting to the resource.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            The user name for connecting to the resource.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return The password for connecting to the resource.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            The password for connecting to the resource.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}
