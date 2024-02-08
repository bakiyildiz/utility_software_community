package org.by.usc.notifier.economy.controller;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.by.usc.common.COMMON;
import org.by.usc.common.model.EconomyReport;
import org.by.usc.common.model.NotifierParameter;
import org.by.usc.notifier.economy.NotifierEconomy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * @author baki.yildiz
 *
 */
public class ValueChecker extends COMMON {
	
	public static boolean check(NotifierParameter parameters) {
		
		double oldValue = 0;
		double newValue = 0;
		
		try {
        	log(NotifierEconomy.APP_NAME, "Checking " + parameters.getTitle() + " Value");
        	newValue = getData(parameters);
			
			String[] valConfKeys = {parameters.getValueConfigName()};
			HashMap<String, String> valConfigs = getNotifierEconomyValues(valConfKeys);
			oldValue = Double.valueOf(valConfigs.get(parameters.getValueConfigName()));
			
			if(oldValue != newValue && newValue != 0){
	            DateFormat dateFormatFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	            Date date = new Date();
	            
	            double kontrol = 0;
	            kontrol = (oldValue) - (newValue);
	            
	            String mailSubject = null;
	            String mailContent = null;
	            boolean mailSend = false;
	            
	            String[] confKeys = {parameters.getDifferenceConfigName()};
	    		HashMap<String, String> configs = getNotifierEconomyValues(confKeys);
	    		String value = configs.get(parameters.getDifferenceConfigName());
	    		double dif = Double.valueOf(value);
            	List<EconomyReport> report = getEconomyValueHistory(valConfKeys[0], parameters.getLastValueCount());
	            
	            if(kontrol >= dif){
	            	mailSubject = parameters.getTitle() + " Notify";
	            	mailContent = parameters.getTitle() + " deðeri düþtü.\n\nEski deðer: " + oldValue + ", yeni deðer: " + newValue + " - " + dateFormatFull.format(date);
	            	mailSend = true;
	                oldValue = newValue;
	                updateNotifierEconomyValue(valConfKeys[0], String.valueOf(oldValue));
                    addNotifierEconomyValueHistory(valConfKeys[0], String.valueOf(newValue), false);
	            }else if(kontrol <= -dif){
	            	mailSubject = parameters.getTitle() + " Notify";
	            	mailContent = parameters.getTitle() + " deðeri yükseldi.\n\nEski deðer: " + oldValue + ", yeni deðer: " + newValue + " - " + dateFormatFull.format(date);
	            	mailSend = true;
	                oldValue = newValue;
	                updateNotifierEconomyValue(valConfKeys[0], String.valueOf(oldValue));
                    addNotifierEconomyValueHistory(valConfKeys[0], String.valueOf(newValue), true);
	            }
	            
	            if(mailSend) {
                	mailContent += "\n\nÖnceki Hareketler:";
                    for (EconomyReport economyReport : report) {
                    	mailContent += "\n" + economyReport.getValue() + " " + (economyReport.getIsIncrease() ? "+" : "-") + " " + economyReport.getDate();
					}
                	
	            	List<String> mailList = getMailList(parameters.getNotifierName());
	                for(String mailTo : mailList) {
	                	insertNewMail(parameters.getNotifierName(), mailTo, mailSubject, mailContent);
	                }
	            }
	        }
		} catch (Exception e) {
			e.printStackTrace();
			log(NotifierEconomy.APP_NAME, "check " + parameters.getTitle() + " Exception: " + e);
			return false;
		}
		
		return true;
	}
	
	private static double getData(NotifierParameter parameters) throws Exception {
        try {
        	Document doc = Jsoup.connect(parameters.getUrl()).userAgent("mozilla/17.0").timeout(20000).get();
        	Elements elementByID = doc.select(parameters.getCheckCode());
            String stringValue = elementByID.text();
            
            if(parameters.getReplaceTarget() != null && parameters.getReplacement() != null)
            	stringValue = stringValue.replace(parameters.getReplaceTarget(), parameters.getReplacement());
            
            stringValue = stringValue.replace(",", "."); // default
            
            if(parameters.getRemoveDigit() > 0)
            	stringValue = stringValue.substring(0, stringValue.length()-parameters.getRemoveDigit());
            
            String format = "##.";
            for (int i = 0; i < parameters.getDigitCount(); i++)
				format += "#";
            
            stringValue = new DecimalFormat(format).format(Double.valueOf(stringValue));
            stringValue = stringValue.replace(",", ".");

            return Double.valueOf(stringValue.replace(",", "."));
        } catch (Exception e) {
        	e.printStackTrace();
        	log(NotifierEconomy.APP_NAME, "getData Exception: " + e);
        	throw new Exception(e);
        }        
    }

}
