package org.by.usc.notifier.economy.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.by.usc.common.COMMON;
import org.by.usc.notifier.economy.NotifierEconomy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author baki.yildiz
 *
 */
public class ValueChecker extends COMMON {
	
	public static void check(String notifierName, String checkType, String checkCode, String title, String valueConfigName, String differenceConfigName, int removeDigit, String replace){
		
		// GauUsdNotifier, gau-usd, Gau/Usd, GauUsdValue, GauUsdDifference
		
		double oldValue = 0;
		double newValue = 0;
		
		try {
        	log(NotifierEconomy.APP_NAME, "Checking " + title + " Value");
        	newValue = getData(checkType, checkCode, removeDigit, replace);
		} catch (Exception e) {
			e.printStackTrace();
        	log(NotifierEconomy.APP_NAME, "check " + title + " Exception: " + e);
		}
		
		String[] valConfKeys = {valueConfigName};
		HashMap<String, String> valConfigs = getConfigs(NotifierEconomy.APP_NAME, valConfKeys);
		oldValue = Double.valueOf(valConfigs.get(valueConfigName));
		
		if(oldValue != newValue && newValue != 0){
            DateFormat dateFormatFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            
            double kontrol = 0;
            kontrol = (oldValue) - (newValue);
            
            String mailSubject = null;
            String mailContent = null;
            boolean mailSend = false;
            
            String[] confKeys = {differenceConfigName};
    		HashMap<String, String> configs = getConfigs(NotifierEconomy.APP_NAME, confKeys);
    		String value = configs.get(differenceConfigName);
    		double dif = Double.valueOf(value);
            
            if(kontrol >= dif){
            	mailSubject = title + " Notify";
            	mailContent = title + " deðeri düþtü.\n\nEski deðer: " + oldValue + ", yeni deðer: " + newValue + " - " + dateFormatFull.format(date);
            	mailSend = true;
                oldValue = newValue;
                updateConfigs(NotifierEconomy.APP_NAME, valConfKeys, String.valueOf(oldValue));
            }else if(kontrol <= -dif){
            	mailSubject = title + " Notify";
            	mailContent = title + " deðeri yükseldi.\n\nEski deðer: " + oldValue + ", yeni deðer: " + newValue + " - " + dateFormatFull.format(date);
            	mailSend = true;
                oldValue = newValue;
                updateConfigs(NotifierEconomy.APP_NAME, valConfKeys, String.valueOf(oldValue));
            }
            
            if(mailSend) {
            	List<String> mailList = getMailList(notifierName);
                for(String mailTo : mailList) {
                	insertNewMail(notifierName, mailTo, mailSubject, mailContent);
                }
            }
        }
		
	}
	
	private static double getData(String checkType, String checkCode, int removeDigit, String replace) throws Exception {
        try {
            Document doc = Jsoup.connect("https://investing.com/" + checkType + "/" + checkCode).userAgent("mozilla/17.0").timeout(10000).get();
        	Element elementByID = doc.getElementById("last_last");
            String stringValue = elementByID.text();
            stringValue = stringValue.substring(0, stringValue.length()-removeDigit);
            if(replace != null)
            	stringValue = stringValue.replace(replace, "");
            return Double.valueOf(stringValue);
        } catch (Exception e) {
        	e.printStackTrace();
        	log(NotifierEconomy.APP_NAME, "getData Exception: " + e);
        	throw new Exception(e);
        }        
    }

}
