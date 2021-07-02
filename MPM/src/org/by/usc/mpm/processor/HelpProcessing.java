package org.by.usc.mpm.processor;

import java.util.HashMap;
import java.util.List;

import org.by.usc.common.COMMON;
import org.by.usc.common.model.Mail;
import org.by.usc.mpm.MPM;

/**
 * @author baki.yildiz
 *
 */
public class HelpProcessing extends COMMON{
	
	public static void process(Mail mail) throws Exception {
		try {
			
			boolean isAdmin = false;
			
			List<String> mailList = getMailList("SysAdm");
            for(String mailTo : mailList) {
            	if(mailTo.equalsIgnoreCase(mail.getMailTo())) {
            		isAdmin = true;
            		break;
            	}
            }
            
            String subject = null;
			String content = null;
            
            if(isAdmin) {
            	String[] confKeys = {"HelpDetail"};
    			HashMap<String, String> configs = getConfigs("Help", confKeys);
    			String check = configs.get("HelpDetail");
    			
    			subject = "Help";
    			content = check;
            }else {
            	subject = "Help";
            	content = "Bu iþlem için yetkiniz bulunmamaktadýr!";
            }
			
			insertNewMail(MPM.APP_NAME, mail.getMailTo(), subject, content);
		} catch (Exception e) {
			log(MPM.APP_NAME, "Processing mail exception: " +  e);
			throw e;
		}
	}

}
