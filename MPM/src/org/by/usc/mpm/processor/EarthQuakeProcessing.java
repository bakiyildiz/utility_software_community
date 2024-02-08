package org.by.usc.mpm.processor;

import java.util.List;

import org.by.usc.common.COMMON;
import org.by.usc.common.model.Mail;
import org.by.usc.mpm.MPM;

/**
 * @author baki.yildiz
 *
 */
public class EarthQuakeProcessing extends COMMON {
	
	private static final String mailSubject = "Deprem Mail Listesi";
	private static final String Earthquake_Notifier = "EarthquakeNotifier";
	
	public static void process(Mail mail, Boolean isAddProcess) throws Exception {
		try {
			List<String> mailList = getMailList(Earthquake_Notifier);
			boolean isMailInList = false;
            for(String mailTo : mailList)
            	if(mailTo.equalsIgnoreCase(mail.getMailTo())) {
            		isMailInList = true;
            		break;
            	}
            
            String content = null;
            
            if(isAddProcess) {
            	if(isMailInList) {
            		content = mail.getMailTo() + " adresi listede mevcut!";
            	} else {
            		List<String> mailListWithDisabled = getMailListWithDisabled(Earthquake_Notifier);
        			isMailInList = false;
                    for(String mailTo : mailListWithDisabled)
                    	if(mailTo.equalsIgnoreCase(mail.getMailTo())) {
                    		isMailInList = true;
                    		break;
                    	}
                    
            		if(isMailInList)
                		updateStatusFromMailList("Y", Earthquake_Notifier, mail.getMailTo());
            		else
            			addToMailList(Earthquake_Notifier, mail.getMailTo());
            		
            		content = mail.getMailTo() + " adresi listeye eklenmiþtir.";
            	}
            } else {
            	if(!isMailInList) {
            		content = mail.getMailTo() + " adresi listede mevcut deðil!";
            	} else {
            		updateStatusFromMailList("N", Earthquake_Notifier, mail.getMailTo());
            		content = mail.getMailTo() + " adresi listeden silinmiþtir.";
            	}
            }
			
			insertNewMail(MPM.APP_NAME, mail.getMailTo(), mailSubject, content);
		} catch (Exception e) {
			log(MPM.APP_NAME, "Processing mail exception: " +  e);
			throw e;
		}
	}

}
