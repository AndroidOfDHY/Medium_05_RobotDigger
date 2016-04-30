package com.sunrise.robotdigger.hibernate;

import java.sql.Timestamp;

/**
 * Uploadfile entity. @author MyEclipse Persistence Tools
 */
public class Uploadfile extends AbstractUploadfile implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public Uploadfile() {
	}

	/** full constructor */
	public Uploadfile(String extractnum, Users users, String filename,
			String filepath, Timestamp uploadtime) {
		super(extractnum, users, filename, filepath, uploadtime);
	}

}
