package org.by.usc.mpm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.by.usc.common.COMMON;
import org.by.usc.common.model.Mail;
import org.by.usc.mpm.processor.EconomyProcessing;
import org.by.usc.mpm.processor.HelpProcessing;
import org.by.usc.mpm.processor.MessageProcessing;
import org.by.usc.mpm.processor.ReminderProcessing;

/**
 * @author baki.yildiz
 *
 * Mail Processing Module
 *
 */
public class MPM extends COMMON{
	
	public static String APP_NAME = "MPM";
	private static Connection conn = null;

	public static void main(String[] args) {
		
		try {
			while(canItWork(APP_NAME)) {
				Thread.sleep(10000);
				log(APP_NAME, "Checking processing mails..");
				checkNewProcessingMail();
			}
			log(APP_NAME, "Kill signal!");
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "main exception: " + e);
		}
	}
	
	private static void checkNewProcessingMail() {
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = checkConnectDb(conn);
			
			String query = "select * from Rasp.Mail_Processing where status = 'N'";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			
			List<Mail> mailList = new ArrayList<Mail>();
			
			while (rs.next()) {
				Mail mail = new Mail(rs.getString("ID"), rs.getString("MAIL_SUBJECT"), null, rs.getString("MAIL_FROM"));
				mailList.add(mail);
			}
			
			log(APP_NAME, "New processing mail count: " + mailList.size());
			
			if(mailList.size() > 0) {
				log(APP_NAME, "Processing mails..");
				
				for (Mail mail : mailList) {
					try {
						String[] params = mail.getMailSubject().split(";");
						boolean processIsSuccess = false;
						
						if(params[0].equalsIgnoreCase("Selam")) {
							MessageProcessing.process(mail);
							processIsSuccess = true;
						}else if(params[0].equalsIgnoreCase("EkonomikVeriler")){
							EconomyProcessing.process(mail);
							processIsSuccess = true;
						}else if(params[0].equalsIgnoreCase("AddReminder")){
							ReminderProcessing.process(mail, params);
							processIsSuccess = true;
						}else if(params[0].equalsIgnoreCase("help")){
							HelpProcessing.process(mail);
							processIsSuccess = true;
						}else {
							String message = "Talebiniz geçersizdir, lütfen kontrol edip tekrar deneyin. Ayrýca talebin içerisinde boþluk bulunmadýðýndan emin olun.";
							insertNewMail(APP_NAME, mail.getMailTo(), APP_NAME, message);
							processIsSuccess = false;
						}
						
						if(processIsSuccess) {
							markProcessingMail(mail, "S", null);
						}else {
							markProcessingMail(mail, "F", "Undefined process: " + params[0]);
						}
						
						log(APP_NAME, "Processing mail success, mail_id " + mail.getMailId());
					} catch (Exception e) {
						e.printStackTrace();
						log(APP_NAME, "checkNewProcessingMail Exception: " + e);
						markProcessingMail(mail, "F", e.getMessage());
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "checkNewProcessingMail Exception: " + e);
		} finally {
			closeParam(null, stmt, rs, null);
		}
	}
	
	private static void markProcessingMail(Mail mail, String status, String detail) {
		PreparedStatement update;
		try {
			
			String query = "update Rasp.Mail_Processing set STATUS = ?, DETAIL = ?, UDATE = SYSDATE() WHERE ID = ?";
			update = conn.prepareStatement(query);
			update.setString(1, status);
			update.setString(2, detail);
			update.setString(3, mail.getMailId());
			update.executeUpdate();
			closeParam(null, null, null, update);
		} catch (SQLException e) {
			e.printStackTrace();
			log(APP_NAME, "markProcessingMail Exception: " + e);
		}
	}
}
