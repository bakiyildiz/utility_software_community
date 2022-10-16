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
			if(conn != null)
				conn.close();
			
			if(stmt != null)
				stmt.close();
			
			if(rs != null)
				rs.close();
			
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
			
			closeParam(conn, null, rs, ps);
			
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "getConfigs Exception: " + e);
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
			
			closeParam(conn, null, rs, ps);
			
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "getMailList Exception: " + e);
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
			
			closeParam(conn, null, null, ps);
			
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "updateConfigs Exception: " + e);
		}
	}
	
	public static void updateNotifierEconomyValue(String title, String Value){
		PreparedStatement ps = null;
		Connection conn = null;
		
		try {
			
			log(APP_NAME, "updateNotifierEconomyValue, title: " + title + ", Value: " + Value);
			
			conn = connectDb();
			String query = "UPDATE Rasp.Notifier_Economy_Values SET VALUE=? WHERE TITLE = ?";
			
			ps = conn.prepareStatement(query);
			ps.setString(1, Value);
			ps.setString(2, title);
			
			ps.executeUpdate();
			
			closeParam(conn, null, null, ps);
			
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "updateNotifierEconomyValue Exception: " + e);
		}
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
			
			closeParam(conn, null, rs, ps);
			
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "getNotifierEconomyValues Exception: " + e);
		}
		
		return configs;
	}
	
	public static void insertNewMail(String moduleName, String mailTo, String mailSubject, String mailContent) {
		try {
			Connection conn = null;
					
			conn = checkConnectDb(conn);
			
			PreparedStatement insert;
				
			String query = "insert into Rasp.Mail_Events (MODULE_NAME, CDATE, UDATE, MAIL_SUBJECT, MAIL_CONTENT, MAIL_TO, STATUS, DETAIL)";
			query += " values (?, sysdate(), null, ?, ?, ?, 'N', null)";
			
			insert = conn.prepareStatement(query);
			insert.setString(1, moduleName);
			insert.setString(2, mailSubject);
			insert.setString(3, mailContent);
			insert.setString(4, mailTo);
			insert.executeUpdate();
			closeParam(conn, null, null, insert);
			
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "insertNewMail Exception: " + e);
		}
	}
	
	public static void insertNewProcessingMail(String mailFrom, String mailSubject, String mailDate) {
		try {
			Connection conn = null;
			conn = checkConnectDb(conn);
			PreparedStatement insert;
				
			String query = "insert into Rasp.Mail_Processing (CDATE, MAIL_FROM, MAIL_SUBJECT, STATUS)";
			query += " values (?, ?, ?, 'N')";
			
			insert = conn.prepareStatement(query);
			insert.setString(1, mailDate);
			insert.setString(2, mailFrom);
			insert.setString(3, mailSubject);
			insert.executeUpdate();
			closeParam(conn, null, null, insert);
			
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "insertNewMail Exception: " + e);
		}
	}
}
