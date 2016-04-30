package com.sunrise.robotdigger.hibernate;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * AbstractUsers entity provides the base persistence definition of the Users
 * entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractUsers implements java.io.Serializable {

	// Fields

	private String username;
	private String password;
	private Timestamp registrationtime;
	private String email;
	private Set uploadfiles = new HashSet(0);

	// Constructors

	/** default constructor */
	public AbstractUsers() {
	}

	/** minimal constructor */
	public AbstractUsers(String username, String password,
			Timestamp registrationtime) {
		this.username = username;
		this.password = password;
		this.registrationtime = registrationtime;
	}

	/** full constructor */
	public AbstractUsers(String username, String password,
			Timestamp registrationtime, String email, Set uploadfiles) {
		this.username = username;
		this.password = password;
		this.registrationtime = registrationtime;
		this.email = email;
		this.uploadfiles = uploadfiles;
	}

	// Property accessors

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Timestamp getRegistrationtime() {
		return this.registrationtime;
	}

	public void setRegistrationtime(Timestamp registrationtime) {
		this.registrationtime = registrationtime;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set getUploadfiles() {
		return this.uploadfiles;
	}

	public void setUploadfiles(Set uploadfiles) {
		this.uploadfiles = uploadfiles;
	}

}