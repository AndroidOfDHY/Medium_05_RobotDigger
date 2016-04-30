package com.sunrise.robotdigger.hibernate;

import java.sql.Timestamp;

/**
 * AbstractUploadfile entity provides the base persistence definition of the
 * Uploadfile entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractUploadfile implements java.io.Serializable {

	// Fields

	private String extractnum;
	private Users users;
	private String filename;
	private String filepath;
	private Timestamp uploadtime;

	// Constructors

	/** default constructor */
	public AbstractUploadfile() {
	}

	/** full constructor */
	public AbstractUploadfile(String extractnum, Users users, String filename,
			String filepath, Timestamp uploadtime) {
		this.extractnum = extractnum;
		this.users = users;
		this.filename = filename;
		this.filepath = filepath;
		this.uploadtime = uploadtime;
	}

	// Property accessors

	public String getExtractnum() {
		return this.extractnum;
	}

	public void setExtractnum(String extractnum) {
		this.extractnum = extractnum;
	}

	public Users getUsers() {
		return this.users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilepath() {
		return this.filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public Timestamp getUploadtime() {
		return this.uploadtime;
	}

	public void setUploadtime(Timestamp uploadtime) {
		this.uploadtime = uploadtime;
	}

}