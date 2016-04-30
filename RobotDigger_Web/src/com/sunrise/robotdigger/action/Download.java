package com.sunrise.robotdigger.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import com.sunrise.robotdigger.hibernate.UploadfileDAO;
import com.sunrise.robotdigger.hibernate.Users;
import com.sunrise.robotdigger.hibernate.UsersDAO;

public class Download extends ActionSupport {
	static final int BUFFER = 2048;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UploadfileDAO uploadfileDAO;
	private String filenum;
	private String filename;
	private UsersDAO usersDAO;
	private String username;
	private String password;
	private String foldername;
	private String CSVname;
	private String message;
	private InputStream input;
	static boolean flag = false;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public String getCSVname() {
		return CSVname;
	}

	public void setCSVname(String cSVname) {
		CSVname = cSVname;
	}

	public String getFoldername() {
		return foldername;
	}

	public void setFoldername(String foldername) {
		this.foldername = foldername;
	}

	public String getFilename() {
		return filename;
	}

	public String getFilenum() {
		return filenum;
	}

	public void setFilenum(String filenum) {
		this.filenum = filenum;
	}

	public UploadfileDAO getUploadfileDAO() {
		return uploadfileDAO;
	}

	public void setUploadfileDAO(UploadfileDAO uploadfileDAO) {
		this.uploadfileDAO = uploadfileDAO;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public UsersDAO getUsersDAO() {
		return usersDAO;
	}

	public void setUsersDAO(UsersDAO usersDAO) {
		this.usersDAO = usersDAO;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSavePath() throws Exception {
		return ServletActionContext.getServletContext().getRealPath(
				"backup/" + username + "/");
	}

	public void downloadFolders() {
		Users users = usersDAO.findById(username);
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		DataOutputStream dos;
		try {
			dos = new DataOutputStream(response.getOutputStream());

			if (users != null)
				if (users.getPassword().equals(password)) {
					FilenameFilter filter = new FilenameFilter() {
						public boolean accept(File dir, String name) {
							return dir.isDirectory();
						}
					};
					File putingFile = new File(getSavePath());
					if (putingFile.exists()) {
						String[] fileList = putingFile.list(filter);
						String filename = "";
						for (String string : fileList)
							filename += string + ",";
						dos.writeUTF(filename);
						return;
					} else {
						dos.writeUTF("0");
						return;
					}
				}
			dos.writeUTF("-1");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void downloadFiles() throws UnsupportedEncodingException {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		DataOutputStream dos;
		try {
			dos = new DataOutputStream(response.getOutputStream());
			File putingFile = new File(getSavePath() + "/"
					+ new String(foldername.getBytes("ISO8859-1"), "UTF-8")
					+ "/");
			if (putingFile.exists()) {
				String[] fileList = putingFile.list();
				String filename = "";
				for (String string : fileList)
					filename += string + ",";
				dos.writeUTF(filename);
				return;
			}
			dos.writeUTF("-1");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String downloadFile() throws Exception {
		filename = new String(CSVname.getBytes("ISO8859-1"), "UTF-8");
		String path = getSavePath() + "/"
				+ new String(foldername.getBytes("ISO8859-1"), "UTF-8") + "/"
				+ new String(CSVname.getBytes("ISO8859-1"), "UTF-8");
		input = new FileInputStream(path);
		System.out.println(filename);
		return "success";
	}

	public String downloadfile() throws FileNotFoundException, UnsupportedEncodingException {
		message = null;
		if (uploadfileDAO.findById(filenum) == null) {
			message = "<script type='text/javascript'>$(document).ready(function() {$('.yzm_tip').show();$('.down').click();});</script>";
			return "false";
		} else {
			filename = uploadfileDAO.findById(filenum).getFilename();
			filename=new String(filename.getBytes("UTF-8"),"ISO8859-1");
			String path = uploadfileDAO.findById(filenum).getFilepath();
			input = new FileInputStream(path);
			return "success";
		}
	}

	public String downloadBackup() {
		try {
			File file = ZipSubdirectory(new File(getSavePath()));
			if (file == null) {
				message = "<script type='text/javascript'>alert('ÉÐÎ´±¸·Ý');</script>";
				return "false";
			}
			input = new FileInputStream(file);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			filename = file.getName().substring(0, file.getName().indexOf("."))
					+ "_" + formatter.format(new Date()) + ".zip";
			return "success";
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "false";
	}

	public static File ZipSubdirectory(File myDir) throws IOException {
		BufferedInputStream origin = null;
		File zipFile = new File(myDir.getName() + ".zip");
		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(fos,
				BUFFER));
		File dirContents[] = myDir.listFiles();
		File tempFile = null;
		if (dirContents == null)
			return null;
		try {
			for (int i = 0; i < dirContents.length; i++) {
				if (dirContents[i].isDirectory()) {
					tempFile = ZipSubdirectory(dirContents[i]);
					flag = true;
				}
				else {
					tempFile = dirContents[i];
					flag = false;
				}
				System.out.println("Compress file: " + tempFile.getName());
				FileInputStream fis = new FileInputStream(tempFile);
				origin = new BufferedInputStream(fis, BUFFER);
				ZipEntry entry = new ZipEntry(tempFile.getName());
				byte data[] = new byte[BUFFER];
				int count;
				out.putNextEntry(entry);
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return zipFile;
	}
	public String downloadApk() throws FileNotFoundException {
		input=new FileInputStream(ServletActionContext.getServletContext().getRealPath("apk/RobotDigger1.0.apk"));
		filename="RobotDigger1.0.apk";
		return SUCCESS;
	}
}
