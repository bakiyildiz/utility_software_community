package org.by.usc.reminder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.by.usc.common.COMMON;
import org.by.usc.reminder.model.ReminderModel;

/**
 * @author baki.yildiz
 * 
 * Notifier Economy
 *
 */
public class Reminder extends COMMON{
	
	public static String APP_NAME = "Reminder";
	private static Connection conn = null;
	
	public static enum Repeat {
		FOR_ONCE, DAILY, WEEKLY, MONTHLY
	}

	public static void main(String[] args) {
		try {
			while(canItWork(APP_NAME)) {
				Thread.sleep(60000);
				log(APP_NAME, "Checking reminders..");
				checkReminders();
			}
			
			log(APP_NAME, "Kill signal!");
		} catch (Exception e) {
			e.printStackTrace();
        	log(APP_NAME, "main Exception: " + e);
		}

	}
	
	private static void checkReminders() {
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = checkConnectDb(conn);
			
			String query = "select * from Rasp.Reminder where is_enabled = 'Y'";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			
			List<ReminderModel> reminderList = new ArrayList<ReminderModel>();
			
			while (rs.next()) {
				ReminderModel reminder = new ReminderModel(rs.getString("ID"), rs.getString("CDATE"), rs.getString("REMINDER_EMAIL"), rs.getString("REMINDER_CONTENT")
						, rs.getString("REMINDER_REPEAT"), rs.getString("REMINDER_DATE"), rs.getString("PROCESS_DATE"), rs.getString("REPEAT_COUNT"));
				reminderList.add(reminder);
			}
			
			log(APP_NAME, "New reminder count: " + reminderList.size());
			
			if(reminderList.size() > 0) {
				log(APP_NAME, "Checking reminders..");
				checkReminders(reminderList);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "checkReminders Exception: " + e);
		} finally {
			closeParam(null, stmt, rs, null);
		}
	}
	
	private static void checkReminders(List<ReminderModel> reminderList) {

		for (ReminderModel reminder : reminderList) {
			try {
				log(APP_NAME, "Checking reminder, reminder_id " + reminder.getId());
				
				Date now = new Date();
				Calendar cNow = Calendar.getInstance();
				cNow.setTime(now);
				
				if(reminder.getReminderRepeat().equalsIgnoreCase(Repeat.FOR_ONCE.toString())) {
					
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
					String dateInString = reminder.getReminderDate();
					Date t1 = formatter.parse(dateInString);
					Calendar c1 = Calendar.getInstance();
					c1.setTime(t1);
					
					if(cNow.getTime().after(c1.getTime())) {
						insertNewMail(APP_NAME, reminder.getReminderEmail(), APP_NAME, reminder.getReminderContent());
						updateReminder(reminder, "N");
						log(APP_NAME, "Reminder success.");
					}else {
						log(APP_NAME, "Time is not up for reminder: " + reminder.getId());
					}
				} else {
					
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
					String dateInString = null;
					
//					if(reminder.getProcessDate() != null) {
//						dateInString = reminder.getProcessDate();
//					} else {
						dateInString = reminder.getReminderDate();
//					}
					
					Date t1 = formatter.parse(dateInString);
					Calendar c1 = Calendar.getInstance();
					c1.setTime(t1);
					
					int repeatCnt = 0;
					
					if(reminder.getRepeatCount() == null || reminder.getRepeatCount().equals("") || Integer.valueOf(reminder.getRepeatCount()) == 0) {
						repeatCnt = 1;
						reminder.setRepeatCount(String.valueOf(repeatCnt));
					} else {
						repeatCnt = Integer.valueOf(reminder.getRepeatCount()) + 1;
						reminder.setRepeatCount(String.valueOf(repeatCnt));
					}
					
					if(reminder.getReminderRepeat().equalsIgnoreCase(Repeat.DAILY.toString())) {
						c1.add(Calendar.DAY_OF_YEAR, 1 * repeatCnt);
					} else if(reminder.getReminderRepeat().equalsIgnoreCase(Repeat.WEEKLY.toString())) {
						c1.add(Calendar.DAY_OF_YEAR, 7 * repeatCnt);
					} else if(reminder.getReminderRepeat().equalsIgnoreCase(Repeat.MONTHLY.toString())) {
						c1.add(Calendar.MONTH, 1 * repeatCnt);
					}
					
					if(cNow.getTime().after(c1.getTime())) {
						insertNewMail(APP_NAME, reminder.getReminderEmail(), APP_NAME, reminder.getReminderContent());
						updateReminder(reminder, "Y");
						log(APP_NAME, "Reminder success.");
					}else {
						log(APP_NAME, "Time is not up for reminder: " + reminder.getId());
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				log(APP_NAME, "checkReminders Exception: " + e);
			}
		}
	}
	
	private static void updateReminder(ReminderModel reminder, String isEnabled) {
		PreparedStatement update;
		try {
			
			String query = "update Rasp.Reminder set UDATE = SYSDATE(), PROCESS_DATE = SYSDATE(), IS_ENABLED = ?";
			
			boolean repeatControl = isEnabled.equalsIgnoreCase("Y");
			
			if(repeatControl)
				query += ", REPEAT_COUNT = ?";
			
			query += " WHERE ID = ?";
				
			update = conn.prepareStatement(query);
			update.setString(1, isEnabled);
			
			if(repeatControl) {
				update.setString(2, reminder.getRepeatCount());
				update.setString(3, reminder.getId());
			} else {
				update.setString(2, reminder.getId());
			}
			
			update.executeUpdate();
			closeParam(null, null, null, update);
		} catch (SQLException e) {
			e.printStackTrace();
			log(APP_NAME, "updateReminder Exception: " + e);
		}
	}

}
