package org.by.usc.mpm.processor;

import org.by.usc.common.COMMON;
import org.by.usc.common.model.Mail;
import org.by.usc.mpm.MPM;

/**
 * @author baki.yildiz
 *
 */
public class MessageProcessing extends COMMON {

	public static void process(Mail mail) {
		try {
			String subject = "Merhaba";
			String content = "Merhaba, nasýlsýnýz?";
			
			insertNewMail(MPM.APP_NAME, mail.getMailTo(), subject, content);
		} catch (Exception e) {
			log(MPM.APP_NAME, "Processing mail exception: " +  e);
			throw e;
		}
	}
}
