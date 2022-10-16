package org.by.usc.notifier.economy;

import org.by.usc.notifier.economy.controller.ValueChecker;

import java.util.List;
import org.by.usc.common.COMMON;

/**
 * @author baki.yildiz
 * 
 * Notifier Economy
 *
 */
public class NotifierEconomy extends COMMON {
	
	public static String APP_NAME = "NotifierEconomy";
	public static Long SLEEP_TIME = 20000L;
	public static Long ERROR_NOTIFY_COUNT = 50L;
	
	public static Long errorCountDolarNotifier = 0L;
	public static Long errorCountEuroNotifier = 0L;
	public static Long errorCountEurUsdNotifier = 0L;
	public static Long errorCountGauUsdNotifier = 0L;
	public static Long errorCountGauTryNotifier = 0L;
	public static Long errorCountCDS5 = 0L;
	public static Long errorCountGauOns = 0L;
	public static Long errorCountUsdEnd = 0L;
	public static Long errorCountBIST100 = 0L;
	public static Long errorCountBrent = 0L;
	
	public static void main(String[] args) {
		try {
			while(canItWork(APP_NAME)) {
				try {
					Thread.sleep(SLEEP_TIME);
					errorCountDolarNotifier = ValueChecker.check("DolarNotifier", "https://www.bloomberght.com/", "#dolar > span > small.value.LastPrice", "USD/TRY", "DolarValue", "DolarDifference",
							2, 0, null, null, errorCountDolarNotifier);
					Thread.sleep(SLEEP_TIME);
					errorCountEuroNotifier = ValueChecker.check("EuroNotifier", "https://www.bloomberght.com/", "#euro > span > small.value.LastPrice", "EUR/TRY", "EuroValue", "EuroDifference", 2, 0,
							null, null, errorCountEuroNotifier);
					Thread.sleep(SLEEP_TIME);
					errorCountEurUsdNotifier = ValueChecker.check("EurUsdNotifier", "https://www.bloomberght.com/", "#eur-usd > span > small.value.LastPrice", "EUR/USD", "EurUsdValue", "EurUsdDifference",
							2, 0, null, null, errorCountEurUsdNotifier);
					Thread.sleep(SLEEP_TIME);
					errorCountGauUsdNotifier = ValueChecker.check("GauUsdNotifier", "https://bigpara.hurriyet.com.tr/altin/altin-kg-dolar-fiyati/",
							"#content > div.contentLeft > div > div.kurDetail.mBot20 > div:nth-child(3) > span.value.up", "GAU/USD", "GauUsdValue", "GauUsdDifference", 1, 3, null, null, errorCountGauUsdNotifier);
					Thread.sleep(SLEEP_TIME);
					errorCountGauTryNotifier = ValueChecker.check("GauTryNotifier", "https://finans.mynet.com/altin/xgld-spot-altin-tl-gr/",
							"body > section > div.row > div.col-12.col-lg-8.col-content > div:nth-child(5) > div > div > div.flex-list-2-col.flex.justify-content-between.data-info-ul-box-m > ul:nth-child(1) > li:nth-child(2) > span:nth-child(2)",
							"GAU/TRY", "GauTryValue", "GauTryDifference", 0, 0, null, null, errorCountGauTryNotifier);
					Thread.sleep(SLEEP_TIME);
					errorCountCDS5 = ValueChecker.check("CDS5Notifier", "http://www.worldgovernmentbonds.com/cds-historical-data/turkey/5-years/",
							"#post-33 > div > div > div.thecontent > div.font-roboto > div:nth-child(5)", "Türkiye 5 Yýllýk CDS", "CDS5Value",
							"CDS5Difference", 0, 0, null, null, errorCountCDS5);
					Thread.sleep(SLEEP_TIME);
					errorCountGauOns = ValueChecker.check("GauOnsNotifier", "https://www.bloomberght.com/", "#altin-ons > span > small.value.LastPrice", "ONS Altýn", "GauOnsValue",
							"GauOnsDifference", 0, 0, ".", "", errorCountGauOns);
					Thread.sleep(SLEEP_TIME);
					errorCountUsdEnd = ValueChecker.check("UsdEndNotifier", "https://www.marketwatch.com/investing/index/dxy",
							"#maincontent > div.region.region--intraday > div.column.column--aside > div > div.intraday__data > h2 > span",
							"Dolar Endeksi", "UsdEndValue", "UsdEndDifference", 2, 0, null, null, errorCountUsdEnd);
					Thread.sleep(SLEEP_TIME);
					errorCountBIST100 = ValueChecker.check("BIST100Notifier", "https://bigpara.hurriyet.com.tr/borsa/canli-borsa/",
							"#content > div > div.contentLeft > div > div.wideContent.sort-bar-x > div.filterBar.liveStockFilterBar > div > div.stockPrice.node-c",
							"BIST 100 Endeksi", "BIST100Value", "BIST100Difference", 0, 0, null, null, errorCountBIST100);
					Thread.sleep(SLEEP_TIME);
					errorCountBrent = ValueChecker.check("BrentNotifier", "https://www.bloomberght.com/", "#brent-petrol > span > small.value.LastPrice", "Brent Petrol",
							"BrentValue", "BrentDifference", 0, 0, null, null, errorCountBrent);
					
					checkErrorCounts();
				} catch (Exception e) {
					e.printStackTrace();
		        	log(APP_NAME, "main Exception: " + e);
				}
				log(APP_NAME, "Kill signal!");
			}
		} catch (Exception e) {
			e.printStackTrace();
        	log(APP_NAME, "main Exception: " + e);
		}
	}
	
	private static void checkErrorCounts() {
		List<String> mailList = getMailList("SysAdm");
		
		if(errorCountDolarNotifier > ERROR_NOTIFY_COUNT) {
			for(String mailTo : mailList)
	        	insertNewMail(APP_NAME, mailTo, "NotifierEconomy Error", "DolarNotifier hata sayýsý " + ERROR_NOTIFY_COUNT + "'u geçti!");
			errorCountDolarNotifier = 0L;
		}
		
		if(errorCountEuroNotifier > ERROR_NOTIFY_COUNT) {
			for(String mailTo : mailList)
	        	insertNewMail(APP_NAME, mailTo, "NotifierEconomy Error", "EuroNotifier hata sayýsý " + ERROR_NOTIFY_COUNT + "'u geçti!");
			errorCountEuroNotifier = 0L;
		}
		
		if(errorCountEurUsdNotifier > ERROR_NOTIFY_COUNT) {
			for(String mailTo : mailList)
	        	insertNewMail(APP_NAME, mailTo, "NotifierEconomy Error", "EurUsdNotifier hata sayýsý " + ERROR_NOTIFY_COUNT + "'u geçti!");
			errorCountEurUsdNotifier = 0L;
		}
		
		if(errorCountGauUsdNotifier > ERROR_NOTIFY_COUNT) {
			for(String mailTo : mailList)
	        	insertNewMail(APP_NAME, mailTo, "NotifierEconomy Error", "GauUsdNotifier hata sayýsý " + ERROR_NOTIFY_COUNT + "'u geçti!");
			errorCountGauUsdNotifier = 0L;
		}
		
		if(errorCountGauTryNotifier > ERROR_NOTIFY_COUNT) {
			for(String mailTo : mailList)
	        	insertNewMail(APP_NAME, mailTo, "NotifierEconomy Error", "GauTryNotifier hata sayýsý " + ERROR_NOTIFY_COUNT + "'u geçti!");
			errorCountGauTryNotifier = 0L;
		}
		
		if(errorCountCDS5 > ERROR_NOTIFY_COUNT) {
			for(String mailTo : mailList)
	        	insertNewMail(APP_NAME, mailTo, "NotifierEconomy Error", "CDS5 hata sayýsý " + ERROR_NOTIFY_COUNT + "'u geçti!");
			errorCountCDS5 = 0L;
		}
		
		if(errorCountGauOns > ERROR_NOTIFY_COUNT) {
			for(String mailTo : mailList)
	        	insertNewMail(APP_NAME, mailTo, "NotifierEconomy Error", "GauOns hata sayýsý " + ERROR_NOTIFY_COUNT + "'u geçti!");
			errorCountGauOns = 0L;
		}
		
		if(errorCountUsdEnd > ERROR_NOTIFY_COUNT) {
			for(String mailTo : mailList)
	        	insertNewMail(APP_NAME, mailTo, "NotifierEconomy Error", "UsdEnd hata sayýsý " + ERROR_NOTIFY_COUNT + "'u geçti!");
			errorCountUsdEnd = 0L;
		}
		
		if(errorCountBIST100 > ERROR_NOTIFY_COUNT) {
			for(String mailTo : mailList)
	        	insertNewMail(APP_NAME, mailTo, "NotifierEconomy Error", "BIST100 hata sayýsý " + ERROR_NOTIFY_COUNT + "'u geçti!");
			errorCountBIST100 = 0L;
		}
		
		if(errorCountBrent > ERROR_NOTIFY_COUNT) {
			for(String mailTo : mailList)
	        	insertNewMail(APP_NAME, mailTo, "NotifierEconomy Error", "Brent hata sayýsý " + ERROR_NOTIFY_COUNT + "'u geçti!");
			errorCountBrent = 0L;
		}
	}
}
