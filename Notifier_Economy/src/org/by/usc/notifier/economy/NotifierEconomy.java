package org.by.usc.notifier.economy;

import org.by.usc.notifier.economy.controller.ValueChecker;

import java.util.HashMap;
import java.util.List;
import org.by.usc.common.COMMON;
import org.by.usc.common.model.NotifierParameter;

/**
 * @author baki.yildiz
 * 
 * Notifier Economy
 *
 */
public class NotifierEconomy extends COMMON {
	
	public static String APP_NAME = "NotifierEconomy";
	public static Long SLEEP_TIME = 10000L;
	public static Long ERROR_NOTIFY_COUNT = 100L;
	
	public static void main(String[] args) {
		try {
			String[] confKeys = {"ReportLastValueCount", "ErrorNotifyCount"};
			HashMap<String, String> configs = getConfigs("NotifierEconomy", confKeys);
			String reportLastValueCount = configs.get("ReportLastValueCount");
			ERROR_NOTIFY_COUNT = Long.valueOf(configs.get("ErrorNotifyCount"));
			
			List<NotifierParameter> notifierParameterList = getNotifierEconomyConfigs(reportLastValueCount);
			
			while(canItWork(APP_NAME)) {
				try {
					for (NotifierParameter notifierParameter : notifierParameterList) {
						Thread.sleep(SLEEP_TIME);
						if(!ValueChecker.check(notifierParameter))
							notifierParameter.increaseErrorCount();
					}

					notifierParameterList = checkErrorCounts(notifierParameterList);
//					checkErrorCounts();
				} catch (Exception e) {
					e.printStackTrace();
		        	log(APP_NAME, "main Exception: " + e);
				}
			}
			
			log(APP_NAME, "Kill signal!");
		} catch (Exception e) {
			e.printStackTrace();
        	log(APP_NAME, "main Exception: " + e);
		}
	}
	
	private static List<NotifierParameter> checkErrorCounts(List<NotifierParameter> notifierParameterList) {
		List<String> mailList = getMailList("SysAdm");
		
		for (NotifierParameter notifierParameter : notifierParameterList) {
			if(notifierParameter.getErrorCount() > ERROR_NOTIFY_COUNT) {
				for(String mailTo : mailList)
		        	insertNewMail(APP_NAME, mailTo, "NotifierEconomy Error", notifierParameter.getNotifierName() + " hata sayýsý " + ERROR_NOTIFY_COUNT + "'u geçti!");
				
				notifierParameter.resetErrorCount();
			}
		}
		
		return notifierParameterList;
	}
}
