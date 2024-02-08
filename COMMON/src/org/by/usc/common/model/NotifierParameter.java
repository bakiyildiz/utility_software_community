package org.by.usc.common.model;

public class NotifierParameter {
	
	private String notifierName;
	private String url;
	private String checkCode;
	private String title;
	private String valueConfigName;
	private String differenceConfigName;
	private int digitCount;
	private int removeDigit;
	private String replaceTarget;
	private String replacement;
	private Long errorCount;
	private String lastValueCount;

	public NotifierParameter() {
	}

	public NotifierParameter(String notifierName, String url, String checkCode, String title,
			String valueConfigName, String differenceConfigName, int digitCount, int removeDigit, String replaceTarget,
			String replacement, Long errorCount, String lastValueCount) {
		super();
		this.notifierName = notifierName;
		this.url = url;
		this.checkCode = checkCode;
		this.title = title;
		this.valueConfigName = valueConfigName;
		this.differenceConfigName = differenceConfigName;
		this.digitCount = digitCount;
		this.removeDigit = removeDigit;
		this.replaceTarget = replaceTarget;
		this.replacement = replacement;
		this.errorCount = errorCount;
		this.lastValueCount = lastValueCount;
	}

	public String getNotifierName() {
		return notifierName;
	}

	public void setNotifierName(String notifierName) {
		this.notifierName = notifierName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getValueConfigName() {
		return valueConfigName;
	}

	public void setValueConfigName(String valueConfigName) {
		this.valueConfigName = valueConfigName;
	}

	public String getDifferenceConfigName() {
		return differenceConfigName;
	}

	public void setDifferenceConfigName(String differenceConfigName) {
		this.differenceConfigName = differenceConfigName;
	}

	public int getDigitCount() {
		return digitCount;
	}

	public void setDigitCount(int digitCount) {
		this.digitCount = digitCount;
	}

	public int getRemoveDigit() {
		return removeDigit;
	}

	public void setRemoveDigit(int removeDigit) {
		this.removeDigit = removeDigit;
	}

	public String getReplaceTarget() {
		return replaceTarget;
	}

	public void setReplaceTarget(String replaceTarget) {
		this.replaceTarget = replaceTarget;
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}

	public Long getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(Long errorCount) {
		this.errorCount = errorCount;
	}

	public String getLastValueCount() {
		return lastValueCount;
	}

	public void setLastValueCount(String lastValueCount) {
		this.lastValueCount = lastValueCount;
	}
	
	public void increaseErrorCount() {
		this.errorCount++;
	}
	
	public void resetErrorCount() {
		this.errorCount = 0L;
	}
}
