package org.by.usc.mdm;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.by.usc.common.COMMON;
import org.by.usc.common.model.Mail;

/**
 * @author baki.yildiz
 * 
 * Mail Delivery Module
 *
 */
public class MDM extends COMMON {
	
	public static String APP_NAME = "MDM";
	private static Connection conn = null;
	
	static String[] mailConfigKeys = {"USERNAME", "PASSWORD", "PORT", "HOST", "MAIL_SEND_HEADER"};
	static HashMap<String, String> getMailConfigs = null;
	static HashMap<String, String> sendMailConfigs = null;
	
	static String getMailHost = null;
	static String getMailUser = null;
	static String getMailPassword = null;
	static String getMailPort = null;
	
	static String sendMailHost = null;
	static String sendMailUser = null;
	static String sendMailPassword = null;
	static String sendMailPort = null;
	static String sendMailHeader = null;
	
	static Store emailStore;
    static Folder emailFolder;

	public static void main(String[] args) {
		try {
			
			getMailConfigs = getConfigs("MAIL_IN", mailConfigKeys);
			
			getMailHost = getMailConfigs.get("HOST");
			getMailUser = getMailConfigs.get("USERNAME");
			getMailPassword = getMailConfigs.get("PASSWORD");
			getMailPort = getMailConfigs.get("PORT");
			
			
			sendMailConfigs = getConfigs("MAIL_OUT", mailConfigKeys);
			
			sendMailHost = sendMailConfigs.get("HOST");
			sendMailUser = sendMailConfigs.get("USERNAME");
			sendMailPassword = sendMailConfigs.get("PASSWORD");
			sendMailPort = sendMailConfigs.get("PORT");
			sendMailHeader = sendMailConfigs.get("MAIL_SEND_HEADER");
			
			int waitStep = 1;
			
			while(canItWork(APP_NAME)) {
				Thread.sleep(10000);
				log(APP_NAME, "Checking new mails..");
				checkNewMail();
				log(APP_NAME, "Checking new processing mails..");
				try {
					checkNewProcessingMail();
				} catch (SocketException | MessagingException | UnknownHostException e) {
					e.printStackTrace();
					log(APP_NAME, "special exception: " + e);
					
					if(waitStep < 7)
						waitStep++;
					log(APP_NAME, "Wating " + 600 * waitStep * 1000 + " second.");
					Thread.sleep(600 * waitStep * 1000);
				}
			}
			log(APP_NAME, "Kill signal!");
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "main exception: " + e);
		}
	}
	
	private static void checkNewMail() {
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = checkConnectDb(conn);
			
			String query = "select * from Rasp.Mail_Events where status = 'N'";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			
			List<Mail> mailList = new ArrayList<Mail>();
			
			while (rs.next()) {
				Mail mail = new Mail(rs.getString("ID"), rs.getString("MAIL_SUBJECT"), rs.getString("MAIL_CONTENT"), rs.getString("MAIL_TO"));
				mailList.add(mail);
			}
			
			log(APP_NAME, "New mail count: " + mailList.size());
			
			if(mailList.size() > 0) {
				log(APP_NAME, "Sending mails..");
				sendMail(mailList);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "checkMail Exception: " + e);
		} finally {
			closeParam(null, stmt, rs, null);
		}
	}
	
	private static void sendMail(List<Mail> mailList) {

		for (Mail mail : mailList) {
			try {
				sendMailProcess(mail);
				markMail(mail, "S", null);
				log(APP_NAME, "Sending mail success, mail_id " + mail.getMailId());
			} catch (Exception e) {
				e.printStackTrace();
				log(APP_NAME, "sendMail Exception: " + e);
				markMail(mail, "F", e.getMessage());
			}
		}
	}
	
	public static void sendMailProcess(Mail mail) throws Exception{
		
		Properties props = new Properties();
		props.put("mail.smtp.host", sendMailHost);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", sendMailPort);
		props.put("mail.smtp.starttls.enable", "true");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(sendMailUser, sendMailPassword);
			}
		});

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sendMailUser));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(mail.getMailTo()));
			message.setSubject(mail.getMailSubject());
			message.setText(mail.getMailContent() + "\n\n" + sendMailHeader);
			
			System.setProperty("java.net.preferIPv4Stack" , "true");

			Transport.send(message);

		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "sendMailProcess Exception: " + e);
			throw e;
		}
    }
	
	public static void markMail(Mail mail, String status, String detail) {
		PreparedStatement update;
		try {
			
			String query = "update Rasp.Mail_Events set STATUS = ?, DETAIL = ?, UDATE = SYSDATE() WHERE ID = ?";
			update = conn.prepareStatement(query);
			update.setString(1, status);
			update.setString(2, detail);
			update.setString(3, mail.getMailId());
			update.executeUpdate();
			closeParam(null, null, null, update);
		} catch (SQLException e) {
			e.printStackTrace();
			log(APP_NAME, "markMail Exception: " + e);
		}
	}
	
	private static void checkNewProcessingMail() throws Exception {
		getMailServerConnection();
		getMailInbox();
	}
	
	public static void getMailServerConnection() throws Exception {
		try {
			
			Properties properties = new Properties();
			properties.put("mail.pop3.host", sendMailHost);
			properties.put("mail.pop3.port", sendMailPort);
			properties.put("mail.pop3.starttls.enable", "true");
			properties.put("mail.smtp.debug", "true");
			Session emailSession = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
	          protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(properties.getProperty("mail.smtp.username"), properties.getProperty("mail.smtp.password"));
	          }
	        });
		
			emailStore = emailSession.getStore("imaps");
			emailStore.connect(sendMailHost, sendMailUser, sendMailPassword);
			emailFolder = emailStore.getFolder("INBOX");
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "getMailServerConnection Exception: " + e);
			throw e;
		}
	}

	public static void getMailInbox() throws Exception {
		try {

			if (emailStore == null || !emailStore.isConnected()) {
				getMailServerConnection();
			}

			emailFolder.open(Folder.READ_WRITE);
			Message messages[] = emailFolder.getMessages();
			Message message;
			
			log(APP_NAME, "Processing mail size: " + messages.length);

			if (messages.length > 0) {
				for (int i = 0; i < messages.length; i++) {
					try {
						message = messages[i];

						String strFrom = message.getFrom()[0].toString();
						strFrom = strFrom.replace(">", "");
						String[] parts = strFrom.split("<");

						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String date = formatter.format(message.getSentDate());

						System.out.println(parts[parts.length - 1].toString());
						System.out.println(date);
						System.out.println(message.getSubject().toString());

						insertNewProcessingMail(parts[parts.length - 1].toString(), message.getSubject().toString(), date);
					} catch (Exception e) {
						e.printStackTrace();
						log(APP_NAME, "getMailInbox Insert Part Exception: " + e);
					}
				}
			}

			if (messages.length > 0) {
				deleteAllMail();
			} else {
				if (emailFolder.isOpen())
					emailFolder.close(false);

				if (emailStore.isConnected())
					emailStore.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "getMailInbox Exception: " + e);
			throw e;
		}
	}
    
	public static void deleteAllMail() {
		try {
			if (emailStore == null || !emailStore.isConnected()) {
				getMailServerConnection();
			}

			Message[] messages = emailFolder.getMessages();
			for (int i = 0; i < messages.length; i++) {
				Message message = messages[i];
				message.setFlag(Flags.Flag.DELETED, true);
			}

			if (emailFolder.isOpen())
				emailFolder.close(true);

			if (emailStore.isConnected())
				emailStore.close();

		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "deleteAllMail Exception: " + e);
		}
	}

}
