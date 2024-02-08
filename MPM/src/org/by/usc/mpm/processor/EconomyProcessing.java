package org.by.usc.mpm.processor;

import java.util.HashMap;
//import java.util.HashMap;
import java.util.List;

import org.by.usc.common.COMMON;
import org.by.usc.common.model.EconomyReport;
import org.by.usc.common.model.Mail;
import org.by.usc.mpm.MPM;
//import org.jsoup.Jsoup;
//import org.jsoup.select.Elements;

/**
 * @author baki.yildiz
 *
 */
public class EconomyProcessing extends COMMON {
	
	public static void process(Mail mail) throws Exception {
		try {
			String[] confKeys = {"ReportLastValueCount"};
			HashMap<String, String> configs = getConfigs("NotifierEconomy", confKeys);
			
			StringBuilder mailContent = new StringBuilder();
			
			List<String> valueNameList = getAllEconomyValueTitle();
			for (String valueName : valueNameList) {
				mailContent.append(getStatement(valueName) + ":\n-----");
				
				List<EconomyReport> valueList = getEconomyValueHistory(valueName, configs.get("ReportLastValueCount"));
				for (EconomyReport economyReport : valueList) {
                	mailContent.append("\n" + economyReport.getValue() + " " + (economyReport.getIsIncrease() ? "+" : "-") + " " + economyReport.getDate());
				}
				
				mailContent.append("\n\n");
			}
			
            insertNewMail(MPM.APP_NAME, mail.getMailTo(), "Ekonomik Veriler", mailContent.toString());
        } catch (Exception e) {
        	log(MPM.APP_NAME, "Processing mail exception: " +  e);
			throw e;
        }
	}
	
	private static String getStatement(String valueName) {
		switch (valueName) {
	    case "BrentValue":
	        return "Brent Petrol (USD)";
	    case "BIST100Value":
	        return "BIST 100 Endeksi";
	    case "UsdEndValue":
	        return "Dolar Endeksi";
	    case "GauOnsValue":
	        return "Ons Altýn";
	    case "CDS5Value":
	        return "Türkiye CDS";
	    case "GauUsdValue":
	        return "Altýn (USD)";
	    case "GauTryValue":
	        return "Altýn (TRY)";
	    case "EurUsdValue":
	        return "Euro / Dolar";
	    case "EuroValue":
	        return "Euro / TL";
	    case "DolarValue":
	        return "Dolar / TL";
	    default:
	        return valueName;
	    }
	}
	
	/*public static void process(Mail mail) throws Exception {
		try {
			
			String strTumEkonomi = null;
			
			String typeId = "id";
			String typeClass = "class";
			
			// Döviz
			
			String siteDolar_TL = "https://investing.com/currencies/usd-try";
			String siteEuro_Dolar = "https://investing.com/currencies/eur-usd";
			String siteEuro_TL = "https://investing.com/currencies/eur-try";
			String siteDolarEndeksi = "https://investing.com/indices/usdollar";
			String siteTL_Yuan = "https://investing.com/currencies/try-cny";
			String sitePound_Dolar = "https://investing.com/currencies/gbp-usd";
			String siteDolar_Ruble = "https://investing.com/currencies/usd-rub";
			
			String dolar_TL = "last_last";
			String dolar_TLDeg = "arial_20 greenFont   pid-18-pc";
			String dolar_TLOrn = "arial_20 greenFont  pid-18-pcp parentheses";
			
			String euro_Dolar = "last_last";
			String euro_DolarDeg = "arial_20 greenFont   pid-1-pc";
			String euro_DolarOrn = "arial_20 greenFont  pid-1-pcp parentheses";
			
			String euro_TL = "last_last";
			String euro_TLDeg = "arial_20 greenFont   pid-66-pc";
			String euro_TLOrn = "arial_20 greenFont  pid-66-pcp parentheses";
			
			String dolarEndeksi = "last_last";
			String dolarEndeksiDeg = "arial_20 greenFont   pid-942611-pc";
			String dolarEndeksiOrn = "arial_20 greenFont  pid-942611-pcp parentheses";
			
			String tl_Yuan = "last_last";
			String tl_YuanDeg = "arial_20 redFont   pid-10249-pc";
			String tl_YuanOrn = "arial_20 redFont  pid-10249-pcp parentheses";
			
			String pound_Dolar = "last_last";
			String pound_DolarDeg = "arial_20 greenFont   pid-2-pc";
			String pound_DolarOrn = "arial_20 greenFont  pid-2-pcp parentheses";
			
			String dolar_ruble = "last_last";
			String dolar_rubleDeg = "arial_20 redFont   pid-2186-pc";
			String dolar_rubleOrn = "arial_20 redFont  pid-2186-pcp parentheses";
			
			// Maden
			
			String siteGramAltin_Dolar = "https://investing.com/currencies/gau-usd";
			String siteGramAltin_TL = "https://investing.com/currencies/gau-try";
			String siteBrentPetrol_Dolar = "https://investing.com/commodities/brent-oil";
			
			String gramAltin_Dolar = "last_last";
			String gramAltin_DolarDeg = "arial_20 greenFont   pid-50654-pc";
			String gramAltin_DolarOrn = "arial_20 greenFont  pid-50654-pcp parentheses";
			
			String gramAltin_TL = "last_last";
			String gramAltin_TLDeg = "arial_20 greenFont   pid-50655-pc";
			String gramAltin_TLOrn = "arial_20 greenFont  pid-50655-pcp parentheses";
			
			String brentPetrol_Dolar = "last_last";
			String brentPetrol_DolarDeg = "arial_20 redFont   pid-8833-pc";
			String brentPetrol_DolarOrn = "arial_20 redFont  pid-8833-pcp parentheses";
			
			// Kripto Para
			
//            String siteBitcoin_Dolar = "https://investing.com/indices/btc-usd";
//            String siteEtheryum_Dolar = "https://investing.com/indices/eth-usd";
//
//            String bitcoin_Dolar = "last_last";
//            String bitcoin_DolarDeg = "arial_20   pid-945629-pc redFont";
//            String bitcoin_DolarOrn = "arial_20  pid-945629-pcp parentheses redFont";
//
//            String etheryum_Dolar = "last_last";
//            String etheryum_DolarDeg = "arial_20   pid-997650-pc redFont";
//            String etheryum_DolarOrn = "arial_20  pid-997650-pcp parentheses redFont";
			
			// Borsa
			
//            String siteBist_100 = "https://investing.com/indices/ise-100";
//
//            String bist_100 = "last_last";
//            String bist_100Deg = "arial_20 greenFont   pid-19155-pc";
//            String bist_100Orn = "arial_20 greenFont  pid-19155-pcp parentheses";
			
			
			// MB Faiz
			
			String siteMB_Faiz = "https://tr.investing.com/central-banks/";
			
			String mbFaiz = "tablesorter genTbl closedTbl crossRatesTbl";
			
			// Sonuçlar
			
			strTumEkonomi = ("Döviz\n---\n");
			
			strTumEkonomi += "\n" + ("Dolar/TL : " + getData(typeId, siteDolar_TL, dolar_TL) + " " + getData(typeClass, siteDolar_TL, dolar_TLDeg)+ " " + getData(typeClass, siteDolar_TL, dolar_TLOrn));
			
			strTumEkonomi += "\n" + ("Euro/Dolar : " + getData(typeId, siteEuro_Dolar, euro_Dolar) + " " +  getData(typeClass, siteEuro_Dolar, euro_DolarDeg) + " " +  getData(typeClass, siteEuro_Dolar, euro_DolarOrn));
			
			strTumEkonomi += "\n" + ("Euro/TL : " + getData(typeId, siteEuro_TL, euro_TL) + " " +  getData(typeClass, siteEuro_TL, euro_TLDeg) + " " +  getData(typeClass, siteEuro_TL, euro_TLOrn));
			
			strTumEkonomi += "\n" + ("Dolar Endeksi : " + getData(typeId, siteDolarEndeksi, dolarEndeksi) + " " +  getData(typeClass, siteDolarEndeksi, dolarEndeksiDeg) + " " +  getData(typeClass, siteDolarEndeksi, dolarEndeksiOrn));
			
			strTumEkonomi += "\n" + ("TL/Yuan : " + getData(typeId, siteTL_Yuan, tl_Yuan) + " " +  getData(typeClass, siteTL_Yuan, tl_YuanDeg) + " " +  getData(typeClass, siteTL_Yuan, tl_YuanOrn));
			
			strTumEkonomi += "\n" + ("Pound/Dolar : " + getData(typeId, sitePound_Dolar, pound_Dolar) + " " +  getData(typeClass, sitePound_Dolar, pound_DolarDeg) + " " +  getData(typeClass, sitePound_Dolar, pound_DolarOrn));
			
			strTumEkonomi += "\n" + ("Dolar/Ruble : " + getData(typeId, siteDolar_Ruble, dolar_ruble) + " " +  getData(typeClass, siteDolar_Ruble, dolar_rubleDeg) + " " +  getData(typeClass, siteDolar_Ruble, dolar_rubleOrn));
			
			strTumEkonomi += "\n" + ("\nMaden\n---\n");
			
			strTumEkonomi += "\n" + ("Gram Altýn/Dolar : " + getData(typeId, siteGramAltin_Dolar, gramAltin_Dolar) + " " +  getData(typeClass, siteGramAltin_Dolar, gramAltin_DolarDeg) + " " +  getData(typeClass, siteGramAltin_Dolar, gramAltin_DolarOrn));
			
			strTumEkonomi += "\n" + ("Gram Altýn/TL : " + getData(typeId, siteGramAltin_TL, gramAltin_TL) + " " +  getData(typeClass, siteGramAltin_TL, gramAltin_TLDeg) + " " +  getData(typeClass, siteGramAltin_TL, gramAltin_TLOrn));
			
			strTumEkonomi += "\n" + ("Brent Petrol/Dolar : " + getData(typeId, siteBrentPetrol_Dolar, brentPetrol_Dolar) + " " +  getData(typeClass, siteBrentPetrol_Dolar, brentPetrol_DolarDeg) + " " +  getData(typeClass, siteBrentPetrol_Dolar, brentPetrol_DolarOrn));
			/*
			 * 
			 * Bu noktadan sonrasý investingten engelleniyor.
			 * 
			 * 
            strTumEkonomi += "\n" + ("\nKripto Para\n---\n");

            strTumEkonomi += "\n" + ("Bitcoin/Dolar : " + getData(typeId, siteBitcoin_Dolar, bitcoin_Dolar) + " " +  getData(typeClass, siteBitcoin_Dolar, bitcoin_DolarDeg) + " " +  getData(typeClass, siteBitcoin_Dolar, bitcoin_DolarOrn));

            strTumEkonomi += "\n" + ("Etheryum/Dolar : " + getData(typeId, siteEtheryum_Dolar, etheryum_Dolar) + " " +  getData(typeClass, siteEtheryum_Dolar, etheryum_DolarDeg) + " " +  getData(typeClass, siteEtheryum_Dolar, etheryum_DolarOrn));

            strTumEkonomi += "\n" + ("\nBorsa\n---\n");

            strTumEkonomi += "\n" + ("Bist 100 : " + getData(typeId, siteBist_100, bist_100) + " " +  getData(typeClass, siteBist_100, bist_100Deg) + " " +  getData(typeClass, siteBist_100, bist_100Orn));

            strTumEkonomi += "\n";
			 */
	/*
			String[] splitTitle = getData(typeClass, siteMB_Faiz, mbFaiz).split("Merkez Bankasý Geçerli Faiz Oraný Sonraki Toplantý Son Deðiþim ");
			String[] splitBanks = splitTitle[1].split("bp\\) ");
			
			strTumEkonomi += "\n" + ("Merkez Bankasý - Geçerli Faiz Oraný - Sonraki Toplantý - Son Deðiþim");
			
			strTumEkonomi += "\n" + ("--------------------------\n");
			
			for (int i = 0; i < splitBanks.length; i++) {
				strTumEkonomi += (splitBanks[i]);
				if(i != splitBanks.length-1){
					strTumEkonomi += "bp" + ")";
					strTumEkonomi += "\n";
				}
			}
			
			String content = (strTumEkonomi);
			String subject = "Ekonomik Veriler";
			
			insertNewMail(MPM.APP_NAME, mail.getMailTo(), subject, content);
		} catch (Exception e) {
			log(MPM.APP_NAME, "Processing mail exception: " +  e);
			throw e;
		}
	}
	
	public static String getData(String type, String site, String name) {
		
		try {
			if (type.equals("class")) {
				org.jsoup.nodes.Document doc = Jsoup.connect(site).userAgent("mozilla/17.0").get();
				Elements elementByID = doc.getElementsByAttributeValueContaining("class", name);
				return String.valueOf(elementByID.text());
			} else {
				org.jsoup.nodes.Element doc = Jsoup.connect(site).userAgent("mozilla/17.0").get();
				org.jsoup.nodes.Element elementByID = doc.getElementById(name);
				return String.valueOf(elementByID.text());
			}
		} catch (Exception e) {
			log(MPM.APP_NAME, "getData exception: " +  e);
			return null;
		}
	}*/
	
}
