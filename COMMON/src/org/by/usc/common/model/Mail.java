package org.by.usc.common.model;

/**
 * @author baki.yildiz
 *
 */
public class Mail {
	
	String mailId;
	String mailSubject;
	String mailContent;
	String mailTo;
	
	public Mail(String mailId, String mailSubject, String mailContent, String mailTo) {
		this.mailId = mailId;
		this.mailSubject = mailSubject;
		this.mailContent = mailContent;
		this.mailTo = mailTo;
	}
	
	public String getMailId() {
		return mailId;
	}
	public void setMailId(String mailId) {
		this.mailId = mailId;
	}
	public String getMailSubject() {
		return mailSubject;
	}
	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}
	public String getMailContent() {
		return mailContent;
	}
	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}
	public String getMailTo() {
		return mailTo;
	}
	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}
}
