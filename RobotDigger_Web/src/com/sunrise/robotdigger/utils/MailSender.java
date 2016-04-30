package com.sunrise.robotdigger.utils;

import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * �����ʼ�����������ߡ������ʼ�
 */
public class MailSender {

	private static final int PORT = 25;

	/**
	 * ���ı���ʽ�����ʼ�
	 * 
	 *            �����͵��ʼ�����Ϣ	
	 */
	public static boolean sendTextMail(String sender, String encryptPassword,
			String smtp, String subject, String content, String file,
			String[] maillists) {
		if (maillists == null || maillists.length == 0
				|| ("".equals(maillists[0].trim()))) {
			return false;
		} else {
			// Get system properties
			Properties props = new Properties();

			// Setup mail server
			props.put("mail.smtp.host", smtp);
			props.put("mail.smtp.port", PORT);
			// Get session
			props.put("mail.smtp.auth", "true"); // �����Ҫ������֤���������false�ĳ�true

			// �ж��Ƿ���Ҫ�����֤
			CustomizedAuthenticator authenticator = null;
			if (true) {
				// �����Ҫ�����֤���򴴽�һ��������֤��
				authenticator = new CustomizedAuthenticator(sender,
						encryptPassword);
			}
			// �����ʼ��Ự���Ժ�������֤������һ�������ʼ���session
			Session sendMailSession = Session.getInstance(props,
					authenticator);
			try {
				// ����session����һ���ʼ���Ϣ
				Message mailMessage = new MimeMessage(sendMailSession);
				// �����ʼ������ߵ�ַ
				Address from = new InternetAddress(sender);
				// �����ʼ���Ϣ�ķ�����
				mailMessage.setFrom(from);
				// �����ʼ��Ľ����ߵ�ַ�������õ��ʼ���Ϣ��
				Address[] tos = null;

				tos = new InternetAddress[maillists.length];
				for (int i = 0; i < maillists.length; i++) {
					tos[i] = new InternetAddress(maillists[i]);
				}

				// Message.RecipientType.TO���Ա�ʾ�����ߵ�����ΪTO
				mailMessage.setRecipients(Message.RecipientType.TO, tos);
				// �����ʼ���Ϣ������
				mailMessage.setSubject(subject);
				// �����ʼ���Ϣ���͵�ʱ��
				mailMessage.setSentDate(new Date());

				BodyPart bodyPart = new MimeBodyPart();
				bodyPart.setText(content);
				
				MimeBodyPart attachPart = null;

				if(file!=null){
				attachPart = new MimeBodyPart();
				DataSource source = new FileDataSource(file);
				attachPart.setDataHandler(new DataHandler(source));
				attachPart.setFileName(file);
				}
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(bodyPart);
				if(file!=null)
				multipart.addBodyPart(attachPart);
				mailMessage.setContent(multipart);
				MailcapCommandMap mc = (MailcapCommandMap) CommandMap
						.getDefaultCommandMap();
				mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
				mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
				mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
				mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
				mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
				CommandMap.setDefaultCommandMap(mc);
				Transport.send(mailMessage);
				return true;
			} catch (MessagingException ex) {
				System.out.println(ex);
			}
		}
		return false;
	}
}
