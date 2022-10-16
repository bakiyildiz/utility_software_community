package org.by.usc.uscm;

import java.sql.Connection;
import org.by.usc.common.COMMON;

/**
 * @author baki.yildiz
 *
 */
public class HealthChecker extends COMMON implements Runnable {

	@Override
	public void run() {
		int errorCount = 0;
		
		while (true) {
			try {
				log(USCM.APP_NAME, "HealthChecker waiting 30 second.");
				Thread.sleep(30000);
				
				log(USCM.APP_NAME, "HealthChecker checking database connection..");
				Connection dbConnection = null;
				dbConnection = checkConnectDb(dbConnection);
				
				if(dbConnection == null || dbConnection.isClosed()) {
					errorCount++;
					log(USCM.APP_NAME, "HealthChecker database connection error! " + errorCount);
				} else {
					log(USCM.APP_NAME, "HealthChecker connection is success.");
					if(errorCount > 0) {
						
						/*String[] confKeys = {"AppNames"};
						HashMap<String, String> configs = getConfigs("App", confKeys);
						String[] uptKeys = configs.get("AppNames").split(",");
						updateConfigs("CanItWork", uptKeys, "Y");*/
						
						Runtime.getRuntime().exec("reboot");
					}
				}
			} catch (Exception e) {
				System.err.println("HealthChecker error: " + e);
			}
		}
	}
}
