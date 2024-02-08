package org.by.usc.uscm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.by.usc.common.COMMON;

public class MDMHealthChecker extends COMMON implements Runnable {

	@Override
	public void run() {
		while (true) {
			try {
				log(USCM.APP_NAME, "MDMHealthChecker waiting 60 second.");
				Thread.sleep(60000);
				
				log(USCM.APP_NAME, "MDMHealthChecker checking mail status..");
				Connection conn = null;
				conn = checkConnectDb(conn);
				
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				String query = "SELECT COUNT(*) FROM Rasp.Mail_Events WHERE STATUS = 'N' AND CDATE < NOW() - INTERVAL 5 MINUTE";

				ps = conn.prepareStatement(query);
				rs = ps.executeQuery();
				
				Long count = 0L;
				
				if(rs.next()) {
					count = Long.valueOf(rs.getString(1));
				}
				
				closeParam(conn, null, rs, ps);
				
				if(count > 0) {
					List<String> mailList = getMailList("SysAdm");
					DateFormat dateFormatFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date startDate = new Date();
		            for(String mailTo : mailList) {
		            	insertNewMail(APP_NAME, mailTo, "Server Restarted", "MDMHealthChecker restarted the server " + dateFormatFull.format(startDate));
		            }
		            
					log(APP_NAME, "MDMHealthChecker Kill signal!");
					Runtime.getRuntime().exec("reboot");
				}
			} catch (Exception e) {
				e.printStackTrace();
	        	log(APP_NAME, "MDMHealthChecker Exception: " + e);
			}
		}
	}

}
