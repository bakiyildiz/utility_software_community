package org.by.usc.common.model;

/**
 * @author baki.yildiz
 *
 */
public class EconomyReport {
	
	String type;
	String value;
	boolean isIncrease;
	String date;
	
	public EconomyReport(String type, String value, boolean isIncrease, String date) {
		this.type = type;
		this.value = value;
		this.isIncrease = isIncrease;
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean getIsIncrease() {
		return isIncrease;
	}

	public void setIsIncrease(boolean isIncrease) {
		this.isIncrease = isIncrease;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
}
