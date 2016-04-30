package com.sunrise.robotdigger.action;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import com.sunrise.robotdigger.hibernate.Users;
import com.sunrise.robotdigger.hibernate.UsersDAO;
import com.sunrise.robotdigger.utils.MailSender;

public class LoginAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UsersDAO usersDAO;
	private String username;
	private String username2;
	private String password;
	private String password2;
	private String passwordRepeat;
	private String passwordNew;
	private String message;
	private String email;
	private String email_name = "robotdigger@163.com";
	private String email_password = "1011050252";
	private String email_smtp = "smtp.163.com";

	public String getUsername2() {
		return username2;
	}

	public void setUsername2(String username2) {
		this.username2 = username2;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public String getPasswordNew() {
		return passwordNew;
	}

	public void setPasswordNew(String passwordNew) {
		this.passwordNew = passwordNew;
	}

	public String getPasswordRepeat() {
		return passwordRepeat;
	}

	public void setPasswordRepeat(String passwordRepeat) {
		this.passwordRepeat = passwordRepeat;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public void phonelogin() {
		Users users = usersDAO.findById(username);
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		DataOutputStream dos;
		try {
			dos = new DataOutputStream(response.getOutputStream());

			if (users != null)
				if (users.getPassword().equals(password))
					dos.writeUTF(users.getEmail()+"!"+users.getRegistrationtime());
			dos.writeUTF("0");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void phoneRegister() {
		Users users = usersDAO.findById(username);
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		DataOutputStream dos;
		try {
			dos = new DataOutputStream(response.getOutputStream());
			if (users == null) {
				usersDAO.save(new Users(username, password, new Timestamp(
						System.currentTimeMillis()), email, null));
				dos.writeInt(1);
			}
			dos.writeInt(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void phoneRetrievePassword() {
		Users users = usersDAO.findById(username);
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		DataOutputStream dos;
		try {
			dos = new DataOutputStream(response.getOutputStream());
			
			boolean isSendSuccessfully = false;
			if (users != null) {
				String[] receivers = { users.getEmail() };
				
				isSendSuccessfully = MailSender
						.sendTextMail(
								email_name,
								email_password,
								email_smtp,
								"RobotDigger",
								"尊敬的Robot digger用户：\n\n您好！\n\nRobot digger用户["
										+ users.getUsername()
										+ "] 在12月27日向Robot digger安全中心申请通过该邮箱找回Robot digger登录密码。\n\n该用户的密码为："
										+ users.getPassword(), null, receivers);
			}
			if (isSendSuccessfully)
				dos.writeInt(1);
			else
				dos.writeInt(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String outlogin() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		session.removeAttribute("RobotUsername");
		return SUCCESS;
	}

	public String login() {
		message = null;
		Users users = usersDAO.findById(username);
		if (users != null)
			if (users.getPassword().equals(password)) {
				HttpServletRequest request = ServletActionContext.getRequest();
				HttpSession session = request.getSession();
				session.setAttribute("RobotUsername", users.getUsername());
			} else {
				message = "<script type='text/javascript'>alert('用户名或密码错误');</script>";
			}
		else {
			message = "<script type='text/javascript'>alert('用户名或密码错误');</script>";
		}
		return SUCCESS;
	}

	public String nologin() {
		message = null;
		Users users = usersDAO.findById(username2);
		if (users != null)
			if (users.getPassword().equals(password2)) {
				HttpServletRequest request = ServletActionContext.getRequest();
				HttpSession session = request.getSession();
				session.setAttribute("RobotUsername", users.getUsername());
			} else {
				message = "<script type='text/javascript'>alert('用户名或密码错误');</script>";
			}
		else {
			message = "<script type='text/javascript'>alert('用户名或密码错误');</script>";
		}
		return SUCCESS;
	}

	public String updatePassword() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		Users users = usersDAO.findById((String) session
				.getAttribute("RobotUsername"));
		if (password.equals(users.getPassword())) {
			if (passwordNew.equals(passwordRepeat)) {
				users.setPassword(passwordNew);
				usersDAO.merge(users);
			}
		} else {
			session.removeAttribute("RobotUsername");
		}
		return SUCCESS;
	}

	public String updateEmail() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		Users users = usersDAO.findById((String) session
				.getAttribute("RobotUsername"));
		if (password.equals(users.getPassword())) {
			users.setEmail(email);
			usersDAO.merge(users);
		} else {
			session.removeAttribute("RobotUsername");
		}
		return SUCCESS;
	}
}
