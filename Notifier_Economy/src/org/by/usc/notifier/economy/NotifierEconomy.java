package org.by.usc.notifier.economy;

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
			while(canItWork(APP_NAME)) {
				ValueChecker.check("DolarNotifier", "https://www.bloomberght.com/", "#dolar > span > small.value.LastPrice", "USD/TRY", "DolarValue", "DolarDifference", 2, 0, null, null);
				Thread.sleep(20000);
				ValueChecker.check("EuroNotifier", "https://www.bloomberght.com/", "#euro > span > small.value.LastPrice", "EUR/TRY", "EuroValue", "EuroDifference", 2, 0, null, null);
				Thread.sleep(20000);
				ValueChecker.check("EurUsdNotifier", "https://www.bloomberght.com/", "#eur-usd > span > small.value.LastPrice", "EUR/USD", "EurUsdValue", "EurUsdDifference", 2, 0, null, null);
				Thread.sleep(20000);
				ValueChecker.check("GauUsdNotifier", "https://bigpara.hurriyet.com.tr/altin/altin-kg-dolar-fiyati/", "#content > div.contentLeft > div > div.kurDetail.mBot20 > div:nth-child(3) > span.value.up", "GAU/USD", "GauUsdValue", "GauUsdDifference", 1, 3, null, null);
				Thread.sleep(20000);
				ValueChecker.check("GauTryNotifier", "https://finans.mynet.com/altin/xgld-spot-altin-tl-gr/", "body > section > div.row > div.col-12.col-lg-8.col-content > div:nth-child(5) > div > div > div.flex-list-2-col.flex.justify-content-between.data-info-ul-box-m > ul:nth-child(1) > li:nth-child(2) > span:nth-child(2)", "GAU/TRY", "GauTryValue", "GauTryDifference", 0, 0, null, null);
				Thread.sleep(20000);
				ValueChecker.check("CDS5", "http://www.worldgovernmentbonds.com/cds-historical-data/turkey/5-years/", "#post-33 > div > div > div.thecontent > div.w3-row > div.w3-col.l4.w3-margin-bottom > div.w3-xlarge > b", "Türkiye 5 Yýllýk CDS", "CDS5Value", "CDS5Difference", 0, 0, null, null);
				Thread.sleep(20000);
				ValueChecker.check("GauOns", "https://www.bloomberght.com/", "#altin-ons > span > small.value.LastPrice", "ONS Altýn", "GauOnsValue", "GauOnsDifference", 0, 0, ".", "");
				Thread.sleep(20000);
				ValueChecker.check("UsdEnd", "https://www.marketwatch.com/investing/index/dxy", "body > div.container.container--body > div.region.region--intraday > div.column.column--aside > div > div.intraday__data > h3 > span", "Dolar Endeksi", "UsdEndValue", "UsdEndDifference", 2, 0, null, null);
				Thread.sleep(20000);
				ValueChecker.check("BIST100", "https://bigpara.hurriyet.com.tr/borsa/canli-borsa/", "#content > div > div.contentLeft > div > div.wideContent.sort-bar-x > div.filterBar.liveStockFilterBar > div > div.stockPrice.node-c", "BIST 100 Endeksi", "BIST100Value", "BIST100Difference", 0, 0, null, null);
				Thread.sleep(20000);
				ValueChecker.check("Brent", "https://www.bloomberght.com/", "#brent-petrol > span > small.value.LastPrice", "Brent Petrol", "BrentValue", "BrentDifference", 0, 0, null, null);
			}
			
			log(APP_NAME, "Kill signal!");
		} catch (Exception e) {
			e.printStackTrace();
        	log(APP_NAME, "main Exception: " + e);
		}
	}
}
