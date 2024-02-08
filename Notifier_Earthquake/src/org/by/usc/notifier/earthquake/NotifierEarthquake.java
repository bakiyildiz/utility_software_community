package org.by.usc.notifier.earthquake;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.by.usc.common.COMMON;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * @author baki.yildiz
 * 
 * Notifier Earthquake
 * 
 */
public class NotifierEarthquake extends COMMON {

	public static String APP_NAME = "NotifierEarthquake";
	public static Long SLEEP_TIME = 60000L;
	
	public static Date lastRecordDate = null;
	
	public static void main(String[] args) {
		try {
			List<String> mailList = null;
			
			while(canItWork(APP_NAME)) {
				try {
					mailList = getMailList("EarthquakeNotifier");
					List<EarthquakeModel> earthquakeModelList = getData("http://www.koeri.boun.edu.tr/scripts/lst5.asp", "body > pre");
					if(earthquakeModelList != null) {
						if(lastRecordDate == null)
							lastRecordDate = new Date();
						
						for (int i = 0; i < earthquakeModelList.size(); i++) {
							Date recordDate = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").parse(earthquakeModelList.get(i).getDate() + " " + earthquakeModelList.get(i).getClock());
							if(Double.valueOf(earthquakeModelList.get(i).getMl()) >= 4 && (recordDate.getTime() - lastRecordDate.getTime()) > 0) {
								for(String mailTo : mailList)
									insertNewMail(APP_NAME, mailTo, "Earthquake Notifier", 
											earthquakeModelList.get(i).getDate() + " " + earthquakeModelList.get(i).getClock() + " tarihinde " + earthquakeModelList.get(i).getPlace()
											+ " (" + earthquakeModelList.get(i).getLatitude() + ", " + earthquakeModelList.get(i).getLongitude() + ") konumunda " + earthquakeModelList.get(i).getDepth()
											+ " KM derinliðinde " + earthquakeModelList.get(i).getMl() + " (ML) büyüklüðünde deprem oldu."
											+ "\n\nVeri kaynaðý Boðaziçi Üniversitesi Kandilli Rasathanesi ve Deprem Araþtýrma Enstitüsü Bölgesel Deprem-Tsunami Ýzleme ve Deðerlendirme Merkezi'dir.");
							}
						}
						
						lastRecordDate = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").parse(earthquakeModelList.get(0).getDate() + " " + earthquakeModelList.get(0).getClock());
					}
					
					Thread.sleep(SLEEP_TIME);
				} catch (Exception e) {
					e.printStackTrace();
		        	log(APP_NAME, "main Exception: " + e);
//		        	
//		        	List<String> adminMailList = getMailList("SysAdm");
//		        	for(String mailTo : adminMailList)
//			        	insertNewMail(APP_NAME, mailTo, "NotifierEarthquake Error", e.getMessage());
				}
			}
			log(APP_NAME, "Kill signal!");
		} catch (Exception e) {
			e.printStackTrace();
        	log(APP_NAME, "main Exception: " + e);
		}
	}
	
	private static List<EarthquakeModel> getData(String url, String checkCode) throws Exception {
        try {
        	List<EarthquakeModel> earthquakeModelList = new ArrayList<EarthquakeModel>();
        	Document doc = Jsoup.connect(url).userAgent("mozilla/17.0").timeout(20000).get();
        	Elements elementByID = doc.select(checkCode);
            String stringValue = elementByID.text();
            
            String[] earthquakeArray = stringValue.split("\n");
            if(earthquakeArray != null && earthquakeArray.length > 0)
            	for (int i=6; i < earthquakeArray.length; i++) {
            		EarthquakeModel earthquakeModel = new EarthquakeModel();
            		String[] earthquakeDetailArray = earthquakeArray[i].replace(" (", "(").replaceAll(" +", " ").split(" ");
            		
            		if(earthquakeDetailArray != null && earthquakeDetailArray.length >= 9) {
            			earthquakeModel.setDate(earthquakeDetailArray[0].trim());
                		earthquakeModel.setClock(earthquakeDetailArray[1].trim());
                		earthquakeModel.setLatitude(earthquakeDetailArray[2].trim());
                		earthquakeModel.setLongitude(earthquakeDetailArray[3].trim());
                		earthquakeModel.setDepth(earthquakeDetailArray[4].trim());
                		earthquakeModel.setMd(earthquakeDetailArray[5].trim());
                		earthquakeModel.setMl(earthquakeDetailArray[6].trim());
                		earthquakeModel.setMw(earthquakeDetailArray[7].trim());
                		earthquakeModel.setPlace(earthquakeDetailArray[8].trim());
                		earthquakeModel.setSolutionAttribute(earthquakeDetailArray[9].trim());
                		
                		earthquakeModelList.add(earthquakeModel);
            		}
				}

            return earthquakeModelList;
        } catch (Exception e) {
        	e.printStackTrace();
        	log(APP_NAME, "getData Exception: " + e);
        	throw e;
        }        
    }

}
