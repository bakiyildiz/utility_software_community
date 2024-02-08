package org.by.usc.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.by.usc.common.model.EconomyReport;
import org.by.usc.common.model.NotifierParameter;

/**
 * @author baki.yildiz
 * 
 * Utility Software Community Common
 *
 */
public class COMMON {
	
	public static String APP_NAME = "COMMON";
	
	private static Connection connectDb() {
		try {
			
			String dbUsername = "";
			String dbPassword = "";
			String dbUrl = "";
			String dbPort = "";
			String dbConnectionParams = "?useUnicode=true&characterEncoding=utf8";
			
			Connection conn = DriverManager.getConnection("jdbc:mysql://" + dbUrl + ":" + dbPort + dbConnectionParams, dbUsername, dbPassword);
			log(APP_NAME, "Connection established.");
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Connection checkConnectDb(Connection conn) {
		try {
			if(conn == null || conn.isClosed()) {
				return connectDb();
			}else {
				return conn;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return conn;
		}
	}
	
	public static void closeParam(Connection conn, Statement stmt, ResultSet rs, PreparedStatement ps) {
		try {
			if(conn != null && !conn.isClosed())
				conn.close();
			
			if(stmt != null && !stmt.isClosed())
				stmt.close();
			
			if(rs != null && !rs.isClosed())
				rs.close();
			
			if(ps != null && !ps.isClosed())
				ps.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "closeParam Exception: " + e);
		}
	}
	
	public static HashMap<String, String> getConfigs(String confName, String[] confKey){
		HashMap<String, String> configs = new HashMap<>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		
		try {
			
			conn = connectDb();
			String query = "select * from Rasp.App_Config where CONFIG_NAME = ? and CONFIG_KEY in (";
			
			for(String str : confKey) {
				query += "'" + str + "',";
			}
			
			query += "'')";
			
			ps = conn.prepareStatement(query);
			ps.setString(1, confName);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				configs.put(rs.getString("CONFIG_KEY"), rs.getString("CONFIG_VALUE"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "getConfigs Exception: " + e);
		} finally {
			closeParam(conn, null, rs, ps);
		}
		
		return configs;
	}
	
	public static List<String> getMailList(String confName){
		List<String> mailList = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		
		try {
			conn = connectDb();
			String query = "select * from Rasp.Mail_List where IS_ENABLED = 'Y' AND LIST_NAME = ?";
			
			ps = conn.prepareStatement(query);
			ps.setString(1, confName);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				mailList.add(rs.getString("VALUE"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "getMailList Exception: " + e);
		} finally {
			closeParam(conn, null, rs, ps);
		}
		
		return mailList;
	}
	
	public static boolean canItWork(String appName) {
		String[] confKeys = {"CanItWork"};
		HashMap<String, String> configs = getConfigs(appName, confKeys);
		String check = configs.get("CanItWork");
		
		if(check != null && check.equalsIgnoreCase("Y")) {
			return true;
		}else {
			return false;
		}
	}
	
	public static void log(String appName, String log) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String strDate = sdf.format(date);
		System.out.println(strDate + " - " + appName + " - " + log);
	}
	
	public static void updateConfigs(String confName, String[] confKey, String newValue){
		PreparedStatement ps = null;
		Connection conn = null;
		
		try {
			
			String confKeys = "";
			for(String str : confKey) {
				confKeys += str + ",";
			}
			log(APP_NAME, "updateConfigs, confName: " + confName + ", newValue: " + newValue + ", confKeys: " + confKeys);
			
			conn = connectDb();
			String query = "UPDATE Rasp.App_Config SET CONFIG_VALUE=? WHERE CONFIG_NAME = ? and CONFIG_KEY in(";
			
			for(String str : confKey) {
				query += "'" + str + "',";
			}
			
			query += "'')";
			
			ps = conn.prepareStatement(query);
			ps.setString(1, newValue);
			ps.setString(2, confName);
			
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "updateConfigs Exception: " + e);
		} finally {
			closeParam(conn, null, null, ps);
		}
	}
	
	public static void updateNotifierEconomyValue(String title, String value){
		PreparedStatement ps = null;
		Connection conn = null;
		
		try {
			
			log(APP_NAME, "updateNotifierEconomyValue, title: " + title + ", value: " + value);
			
			conn = connectDb();
			String query = "UPDATE Rasp.Notifier_Economy_Values SET VALUE=? WHERE TITLE = ?";
			
			ps = conn.prepareStatement(query);
			ps.setString(1, value);
			ps.setString(2, title);
			
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "updateNotifierEconomyValue Exception: " + e);
		} finally {
			closeParam(conn, null, null, ps);
		}
	}
	
	public static void addNotifierEconomyValueHistory(String type, String value, boolean increase){
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		Connection conn = null;
		ResultSet rs = null;
		
		try {
			
			log(APP_NAME, "addNotifierEconomyValueHistory, type: " + type + ", value: " + value);
			
			conn = connectDb();
			
			String countQuery = "SELECT COUNT(1) as COUNT FROM Rasp.Notifier_Economy_Value_History WHERE TYPE = ?";
			ps = conn.prepareStatement(countQuery);
			ps.setString(1, type);
			rs = ps.executeQuery();

			Long count = null;
			
			while (rs.next())
				count = rs.getLong(1);
			
			count++;
			
			String insertQuery = "insert into Rasp.Notifier_Economy_Value_History (COUNT, TYPE, CDATE, VALUE, DIRECTION)";
			insertQuery += " values (?, ?, sysdate(), ?, ?)";
			
			ps2 = conn.prepareStatement(insertQuery);
			ps2.setString(1, count.toString());
			ps2.setString(2, type);
			ps2.setString(3, value);
			ps2.setString(4, increase ? "+" : "-");
			
			ps2.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "addNotifierEconomyValueHistory Exception: " + e);
		} finally {
			closeParam(conn, null, rs, ps);
			closeParam(null, null, null, ps2);
		}
	}
	
	public static List<String> getAllEconomyValueTitle(){
		PreparedStatement ps = null;
		Connection conn = null;
		ResultSet rs = null;

		List<String> reportList = new ArrayList<String>();
		
		try {
			log(APP_NAME, "getAllEconomyValueTitle");
			
			conn = connectDb();
			
			String query = "SELECT * FROM Rasp.Notifier_Economy_Values where TITLE LIKE '%Value'";
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				reportList.add(rs.getString("TITLE"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "getAllEconomyValueTitle");
		} finally {
			closeParam(conn, null, rs, ps);
		}
		
		return reportList;
	}
	
	public static List<EconomyReport> getEconomyValueHistory(String type, String lastValueCount){
		PreparedStatement ps = null;
		Connection conn = null;
		ResultSet rs = null;

		List<EconomyReport> reportList = new ArrayList<EconomyReport>();
		
		try {
			
			log(APP_NAME, "getEconomyValueHistory, type: " + type);
			
			conn = connectDb();
			
			String countQuery = "SELECT * FROM Rasp.Notifier_Economy_Value_History where TYPE = ? order by cdate desc LIMIT " + lastValueCount;
			ps = conn.prepareStatement(countQuery);
			ps.setString(1, type);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				EconomyReport repor = new EconomyReport(type, rs.getString("VALUE"), "+".contentEquals(rs.getString("DIRECTION")), rs.getString("CDATE"));
				reportList.add(repor);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "getEconomyValueHistory Exception: " + e);
		} finally {
			closeParam(conn, null, rs, ps);
		}
		
		return reportList;
	}
	
	public static HashMap<String, String> getNotifierEconomyValues(String[] titleList){
		HashMap<String, String> configs = new HashMap<>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		
		try {
			
			conn = connectDb();
			String query = "select * from Rasp.Notifier_Economy_Values where TITLE in (";
			
			for(String str : titleList) {
				query += "'" + str + "',";
			}
			
			query += "'')";
			
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				configs.put(rs.getString("TITLE"), rs.getString("VALUE"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "getNotifierEconomyValues Exception: " + e);
		} finally {
			closeParam(conn, null, rs, ps);
		}
		
		return configs;
	}
	
	public static void insertNewMail(String moduleName, String mailTo, String mailSubject, String mailContent) {
		Connection conn = null;
		PreparedStatement insert = null;
				
		try {
			conn = checkConnectDb(conn);
				
			String query = "insert into Rasp.Mail_Events (MODULE_NAME, CDATE, UDATE, MAIL_SUBJECT, MAIL_CONTENT, MAIL_TO, STATUS, DETAIL)";
			query += " values (?, sysdate(), null, ?, ?, ?, 'N', null)";
			
			insert = conn.prepareStatement(query);
			insert.setString(1, moduleName);
			insert.setString(2, mailSubject);
			insert.setString(3, mailContent);
			insert.setString(4, mailTo);
			insert.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "insertNewMail Exception: " + e);
		} finally {
			closeParam(conn, null, null, insert);
		}
	}
	
	public static void insertNewProcessingMail(String mailFrom, String mailSubject, String mailDate) {
		Connection conn = null;
		PreparedStatement insert = null;
		try {
			conn = checkConnectDb(conn);
				
			String query = "insert into Rasp.Mail_Processing (CDATE, MAIL_FROM, MAIL_SUBJECT, STATUS)";
			query += " values (?, ?, ?, 'N')";
			
			insert = conn.prepareStatement(query);
			insert.setString(1, mailDate);
			insert.setString(2, mailFrom);
			insert.setString(3, mailSubject);
			insert.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "insertNewMail Exception: " + e);
		} finally {
			closeParam(conn, null, null, insert);
		}
	}
	
	public static void updateStatusFromMailList(String status, String mailListName, String mailAddress) {
		PreparedStatement ps = null;
		Connection conn = null;
		
		try {
			log(APP_NAME, "updateStatusFromMailList, mailListName: " + mailListName + ", mailAddress: " + mailAddress);
			
			conn = connectDb();
			String query = "update Rasp.Mail_List SET IS_ENABLED = ? WHERE LIST_NAME = ? AND VALUE = ?";
			
			ps = conn.prepareStatement(query);
			ps.setString(1, status);
			ps.setString(2, mailListName);
			ps.setString(3, mailAddress);
			
			ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "updateStatusFromMailList Exception: " + e);
		} finally {
			closeParam(conn, null, null, ps);
		}
	}
	
	public static void addToMailList(String mailListName, String mailAddress){
		Connection conn = null;
		PreparedStatement insert = null;
		try {
			conn = checkConnectDb(conn);
				
			String query = "insert into Rasp.Mail_List (LIST_NAME, IS_ENABLED, VALUE)";
			query += " values (?, 'Y', ?)";
			
			insert = conn.prepareStatement(query);
			insert.setString(1, mailListName);
			insert.setString(2, mailAddress);
			insert.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "addToMailList Exception: " + e);
		} finally {
			closeParam(conn, null, null, insert);
		}
	}
	
	public static List<String> getMailListWithDisabled(String confName){
		List<String> mailList = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		
		try {
			conn = connectDb();
			String query = "select * from Rasp.Mail_List where LIST_NAME = ?";
			
			ps = conn.prepareStatement(query);
			ps.setString(1, confName);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				mailList.add(rs.getString("VALUE"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "getMailList Exception: " + e);
		} finally {
			closeParam(conn, null, rs, ps);
		}
		
		return mailList;
	}
	
	public static List<NotifierParameter> getNotifierEconomyConfigs(String lastValueCount){
		PreparedStatement ps = null;
		Connection conn = null;
		ResultSet rs = null;

		List<NotifierParameter> notifierParameterList = new ArrayList<NotifierParameter>();
		
		try {
			
			log(APP_NAME, "getNotifierEconomyConfigs");
			
			conn = connectDb();
			
			String countQuery = "SELECT * FROM Rasp.Notifier_Economy_Configuration where IS_ENABLED = 'Y' order by SEQUENCE";
			ps = conn.prepareStatement(countQuery);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				NotifierParameter notifierParameter = new NotifierParameter(rs.getString("NOTIFIER_NAME"), rs.getString("URL"), rs.getString("CHECK_CODE"), rs.getString("TITLE"),
						rs.getString("VALUE_CONFIG_NAME"), rs.getString("DIFFERENCE_CONFIG_NAME"), Integer.valueOf(rs.getString("DIGIT_COUNT")),
						Integer.valueOf(rs.getString("REMOVE_DIGIT")), rs.getString("REPLACE_TARGET"), rs.getString("REPLACEMENT"), 0L, lastValueCount);
				notifierParameterList.add(notifierParameter);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "getNotifierEconomyConfigs Exception: " + e);
		} finally {
			closeParam(conn, null, rs, ps);
		}
		return notifierParameterList;
	}
}
