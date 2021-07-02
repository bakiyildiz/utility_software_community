package org.by.usc.reminder.model;

public class ReminderModel {

	private String id;
	private String cdate;
	private String reminderEmail;
	private String reminderContent;
	private String reminderRepeat;
	private String reminderDate;
	private String processDate;
	private String repeatCount;
	
	public ReminderModel(String id, String cdate, String reminderEmail, String reminderContent, String reminderRepeat, String reminderDate, String processDate, String repeatCount) {
		this.id = id;
		this.cdate = cdate;
		this.reminderEmail = reminderEmail;
		this.reminderContent = reminderContent;
		this.reminderRepeat = reminderRepeat;
		this.reminderDate = reminderDate;
		this.processDate = processDate;
		this.repeatCount = repeatCount;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getCdate() {
		return cdate;
	}

	public void setCdate(String cdate) {
		this.cdate = cdate;
	}

	public String getReminderEmail() {
		return reminderEmail;
	}
	
	public void setReminderEmail(String reminderEmail) {
		this.reminderEmail = reminderEmail;
	}
	
	public String getReminderContent() {
		return reminderContent;
	}
	
	public void setReminderContent(String reminderContent) {
		this.reminderContent = reminderContent;
	}
	
	public String getReminderRepeat() {
		return reminderRepeat;
	}
	
	public void setReminderRepeat(String reminderRepeat) {
		this.reminderRepeat = reminderRepeat;
	}
	
	public String getReminderDate() {
		return reminderDate;
	}
	
	public void setReminderDate(String reminderDate) {
		this.reminderDate = reminderDate;
	}

	public String getProcessDate() {
		return processDate;
	}

	public void setProcessDate(String processDate) {
		this.processDate = processDate;
	}

	public String getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(String repeatCount) {
		this.repeatCount = repeatCount;
	}
	
}
