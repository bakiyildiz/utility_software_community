package org.by.usc.mpm.processor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import org.by.usc.common.COMMON;
import org.by.usc.common.model.Mail;
import org.by.usc.mpm.MPM;

/**
 * @author baki.yildiz
 *
 */
public class ReminderProcessing extends COMMON {
	
	public static void process(Mail mail, String[] params) throws Exception {
		
		String subject = null;
		String content = null;
		
		try {
			
			if(!params[2].equalsIgnoreCase("FOR_ONCE") && !params[2].equalsIgnoreCase("DAILY") && !params[2].equalsIgnoreCase("WEEKLY") && !params[2].equalsIgnoreCase("MONTHLY")) {
				subject = "Reminder";
				content = "Hatýrlatma formatý hatalý!\n\n" + mail.getMailSubject();
			} else {
				Connection conn = null;
				conn = checkConnectDb(conn);
				PreparedStatement insert;
				
				String query = "insert into Rasp.Reminder (CDATE, IS_ENABLED, REMINDER_EMAIL, REMINDER_CONTENT, REMINDER_REPEAT, REMINDER_DATE)";
				query += " values (sysdate(), 'Y', ?, ?, UPPER(?), ?)";
				
				insert = conn.prepareStatement(query);
				insert.setString(1, mail.getMailTo());
				insert.setString(2, params[1]);
				insert.setString(3, params[2]);
				insert.setString(4, params[3]);
				insert.executeUpdate();
				closeParam(conn, null, null, insert);
				
				subject = "Reminder";
				content = params[3] + " tarihli, " + params[2].toUpperCase() + " döngülü, \"" + params[1] + "\" içerikli reminder kaydýnýz alýndý.";
				
			}
			
			insertNewMail(MPM.APP_NAME, mail.getMailTo(), subject, content);
		} catch (Exception e) {
			log(MPM.APP_NAME, "Processing mail exception: " +  e);
			subject = "Reminder";
			content = "Reminder eklenirken hata oluþtu!\n\n" + mail.getMailSubject();
			insertNewMail(MPM.APP_NAME, mail.getMailTo(), subject, content);
			throw e;
		}
	}

}
