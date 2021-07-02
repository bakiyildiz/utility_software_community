package org.by.usc.notifier.economy;

import org.by.usc.notifier.economy.controller.DolarTryController;
import org.by.usc.notifier.economy.controller.ValueChecker;
import org.by.usc.common.COMMON;

/**
 * @author baki.yildiz
 * 
 * Notifier Economy
 *
 */
public class NotifierEconomy extends COMMON {
	
	public static String APP_NAME = "NotifierEconomy";
	
	public static void main(String[] args) {
		try {
			DolarTryController.checkDolar();
			while(canItWork(APP_NAME)) {
				//Thread.sleep(20000);
				ValueChecker.check("DolarNotifier", "currencies", "usd-try", "USD/TRY", "DolarValue", "DolarDifference", 2, null);
				Thread.sleep(20000);
				ValueChecker.check("EuroNotifier", "currencies", "eur-try", "EUR/TRY", "EuroValue", "EuroDifference", 2, null);
				Thread.sleep(20000);
				ValueChecker.check("EurUsdNotifier", "currencies", "eur-usd", "EUR/USD", "EurUsdValue", "EurUsdDifference", 1, null);
				Thread.sleep(20000);
				ValueChecker.check("GauUsdNotifier", "currencies", "gau-usd", "GAU/USD", "GauUsdValue", "GauUsdDifference", 2, null);
				Thread.sleep(20000);
				ValueChecker.check("GauTryNotifier", "currencies", "gau-try", "GAU/TRY", "GauTryValue", "GauTryDifference", 2, null);
				Thread.sleep(20000);
				ValueChecker.check("CDS5", "rates-bonds", "turkey-cds-5-year-usd-streaming-chart", "Türkiye 5 Yýllýk CDS", "CDS5Value", "CDS5Difference", 2, null);
				Thread.sleep(20000);
				ValueChecker.check("GauOns", "currencies", "xau-usd", "ONS Altýn", "GauOnsValue", "GauOnsDifference", 2, ",");
				Thread.sleep(20000);
				ValueChecker.check("UsdEnd", "indices", "usdollar", "Dolar Endeksi", "UsdEndValue", "UsdEndDifference", 0, null);
				Thread.sleep(20000);
				ValueChecker.check("BIST100", "indices", "ise-100", "BIST 100 Endeksi", "BIST100Value", "BIST100Difference", 2, ",");
				Thread.sleep(20000);
				ValueChecker.check("Brent", "commodities", "brent-oil", "Brent Petrol", "BrentValue", "BrentDifference", 2, null);
			}
			
			log(APP_NAME, "Kill signal!");
		} catch (Exception e) {
			e.printStackTrace();
        	log(APP_NAME, "main Exception: " + e);
		}
	}
}
