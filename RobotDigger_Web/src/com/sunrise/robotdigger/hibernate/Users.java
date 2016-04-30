package com.sunrise.robotdigger.hibernate;

import java.sql.Timestamp;
import java.util.Set;

/**
 * Users entity. @author MyEclipse Persistence Tools
 */
public class Users extends AbstractUsers implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public Users() {
	}

	/** minimal constructor */
	public Users(String username, String password, Timestamp registrationtime) {
		super(username, password, registrationtime);
	}

	/** full constructor */
	public Users(String username, String password, Timestamp registrationtime,
			String email, Set uploadfiles) {
		super(username, password, registrationtime, email, uploadfiles);
	}

}
