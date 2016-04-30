package com.sunrise.robotdigger.action;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;

import com.sunrise.robotdigger.hibernate.Users;
import com.sunrise.robotdigger.hibernate.UsersDAO;

public class UploadFilesAction {

	private File[] file;// 获取上传文件
	private String[] fileFileName;// 获取上传文件名称
	private String[] fileContentType;// 获取上传文件类型
	private String username;
	private String password;
	private String appname;
	private UsersDAO usersDAO;

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public UsersDAO getUsersDAO() {
		return usersDAO;
	}

	public void setUsersDAO(UsersDAO usersDAO) {
		this.usersDAO = usersDAO;
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
	

	public String getSavePath() throws Exception {
		return ServletActionContext.getServletContext().getRealPath(
				"backup/" + username + "/" + appname);
	}

	public File[] getFile() {
		return file;
	}

	public void setFile(File[] file) {
		this.file = file;
	}

	public String[] getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String[] fileFileName) {
		this.fileFileName = fileFileName;
	}

	public String[] getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String[] fileContentType) {
		this.fileContentType = fileContentType;
	}

	public String execute() throws Exception {
		String path = getSavePath();
		System.out.println(path);
		Users users = new Users();
		users.setUsername(username);
		users.setPassword(password);
		if (usersDAO.findByExample(users).size() == 0)
			return "success";
		File savedir = new File(path);
		if (file != null) {
			if (!savedir.exists()) {
				savedir.mkdirs();
			} else {
				delAllFile(path);
			}
			for (int i = 0; i < file.length; i++) {
				File saveFile = new File(savedir, fileFileName[i]);
				FileUtils.copyFile(file[i], saveFile);
			}
		} else {
			if (!savedir.exists()) {
				savedir.mkdirs();
			} else {
				delAllFile(path);
			}
		}
		return "success";
	}

	public boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delFolder(path + "/" + tempList[i]);
				flag = true;
			}
		}
		return flag;
	}

	public void delFolder(String folderPath) {
		try {
			delAllFile(folderPath);
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new File(filePath);
			myFilePath.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
