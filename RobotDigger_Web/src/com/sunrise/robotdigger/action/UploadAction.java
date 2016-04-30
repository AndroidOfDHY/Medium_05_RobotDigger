package com.sunrise.robotdigger.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import com.sunrise.robotdigger.hibernate.Uploadfile;
import com.sunrise.robotdigger.hibernate.UploadfileDAO;
import com.sunrise.robotdigger.hibernate.Users;
import com.sunrise.robotdigger.hibernate.UsersDAO;

/**
 * 获取Android端上传过来的信息
 * 
 * 
 */
@SuppressWarnings("serial")
public class UploadAction extends ActionSupport {
	// 上传文件域
	private File file;
	private String fileContentType;
	private String fileFileName;
	private String savePath;
	private String username;
	private String password;
	private UploadfileDAO uploadfileDAO;
	private UsersDAO usersDAO;
	private String extractnum;

	@Override
	public String execute() {
		FileOutputStream fos = null;
		FileInputStream fis = null;
		Users users = new Users();
		users.setUsername(username);
		users.setPassword(password);
		if (usersDAO.findByExample(users).size() > 0) {
			try {
				System.out.println("获取Android端传过来的普通信息：");
				System.out.println("用户名：" + username);
				System.out.println("密码：" + password);
				System.out.println("获取Android端传过来的文件信息：");
				System.out.println("文件存放目录: " + getSavePath());
				System.out.println("文件名称: " + fileFileName);
				System.out.println("文件大小: " + file.length());
				System.out.println("文件类型: " + fileContentType);
				extractnum = getNum();
				while (uploadfileDAO.findById(extractnum) != null) {
					extractnum = getNum();
				}
				fos = new FileOutputStream(getSavePath() + "/" + extractnum);
				fis = new FileInputStream(getFile());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = fis.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				Uploadfile uploadfile = new Uploadfile();
				uploadfile.setExtractnum(extractnum);
				uploadfile.setFilename(getFileFileName());
				uploadfile.setFilepath(getSavePath() + "/" + extractnum);
				uploadfile.setUploadtime(new Timestamp(System
						.currentTimeMillis()));
				uploadfile.setFilename(getFileFileName());
				uploadfile.setUsers(usersDAO.findById(username));
				uploadfileDAO.save(uploadfile);
				System.out.println("文件上传成功");
			} catch (Exception e) {
				System.out.println("文件上传失败");
				e.printStackTrace();
			} finally {
				close(fos, fis);
			}
		}
		return SUCCESS;
	}

	private String getNum() {
		String number = "";
		int value = 1000000;
		int num = (int) (Math.random() * 1000000);
		while (num < (value / 10)) {
			number += "0";
			value = value / 10;
		}
		number += String.valueOf((int) (num));
		return number;
	}

	private void close(FileOutputStream fos, FileInputStream fis) {
		if (fis != null) {
			try {
				fis.close();
				fis = null;
			} catch (IOException e) {
				System.out.println("FileInputStream关闭失败");
				e.printStackTrace();
			}
		}
		if (fos != null) {
			try {
				fos.close();
				fis = null;
			} catch (IOException e) {
				System.out.println("FileOutputStream关闭失败");
				e.printStackTrace();
			}
		}
	}

	public UsersDAO getUsersDAO() {
		return usersDAO;
	}

	public void setUsersDAO(UsersDAO usersDAO) {
		this.usersDAO = usersDAO;
	}

	public String getSavePath() throws Exception {
		return ServletActionContext.getServletContext().getRealPath(savePath);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public UploadfileDAO getUploadfileDAO() {
		return uploadfileDAO;
	}

	public void setUploadfileDAO(UploadfileDAO uploadfileDAO) {
		this.uploadfileDAO = uploadfileDAO;
	}

	public String getExtractnum() {
		return extractnum;
	}

	public void setExtractnum(String extractnum) {
		this.extractnum = extractnum;
	}

}