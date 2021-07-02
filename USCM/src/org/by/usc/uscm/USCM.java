package org.by.usc.uscm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.by.usc.common.COMMON;

/**
 * @author baki.yildiz
 *
 * Utility Software Community Manager
 *
 */
@SuppressWarnings({"deprecation", "unused"})
public class USCM extends COMMON {
	
	public static String APP_NAME = "USCM";
	
	static HashMap<String, String> globalConfigs;
	
	public static void main(String[] args) {
		try {
			
			HealthChecker healthChecker = new HealthChecker();
			Thread thread = new Thread(healthChecker);
			thread.start();
			
			List<String> mailList = getMailList("SysAdm");
			DateFormat dateFormatFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date startDate = new Date();
            for(String mailTo : mailList) {
            	insertNewMail(APP_NAME, mailTo, "Server Started", "Server Started - " + dateFormatFull.format(startDate));
            }
			
			String[] confKeys = {"CheckClock", "WaitTime"};
			globalConfigs = getConfigs("USCM", confKeys);
			
			boolean isItTime = false;
			
			String[] checkParam = globalConfigs.get("CheckClock").split(":");
			Date t1 = new Date();
			t1.setHours(Integer.valueOf(checkParam[0]));
			t1.setMinutes(Integer.valueOf(checkParam[1]));
			Calendar c1 = Calendar.getInstance();
			c1.setTime(t1);
			
			/*while(canItWork(APP_NAME)) {
				Thread.sleep(60000);
				
				Date now = new Date();
				Calendar cNow = Calendar.getInstance();
				cNow.setTime(now);
				
				if(cNow.getTime().after(c1.getTime())) {
					isItTime = true;
					break;
				}
			}
			
			if(isItTime) {
				thread.stop();
				closeProcess();
			}*/
			
			log(APP_NAME, "Kill signal!");
		} catch (Exception e) {
			e.printStackTrace();
        	log(APP_NAME, "main Exception: " + e);
		}

	}
	
	/*public static void closeProcess() {
		try {
		
			String[] confKeys = {"AppNames"};
			HashMap<String, String> configs = getConfigs("App", confKeys);
			String[] uptKeys = configs.get("AppNames").split(",");
			
			updateConfigs("CanItWork", uptKeys, "N");
			
			int waitTime = Integer.valueOf(globalConfigs.get("WaitTime").replace("minute", "")) * 60 * 1000;
			
			Thread.sleep(waitTime);
			
			updateConfigs("CanItWork", uptKeys, "Y");
			
			Process p = Runtime.getRuntime().exec("reboot");
		} catch (Exception e) {
			e.printStackTrace();
			log(APP_NAME, "closeProcess Exception: " + e);
		}
	}*/

}
