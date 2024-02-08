package org.by.usc.notifier.earthquake;

public class EarthquakeModel {
	
	private String date;
	private String clock;
	private String latitude;
	private String longitude;
	private String depth;
	private String md;
	private String ml;
	private String mw;
	private String place;
	private String solutionAttribute;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getClock() {
		return clock;
	}
	public void setClock(String clock) {
		this.clock = clock;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getDepth() {
		return depth;
	}
	public void setDepth(String depth) {
		this.depth = depth;
	}
	public String getMd() {
		return md;
	}
	public void setMd(String md) {
		this.md = md;
	}
	public String getMl() {
		return ml;
	}
	public void setMl(String ml) {
		this.ml = ml;
	}
	public String getMw() {
		return mw;
	}
	public void setMw(String mw) {
		this.mw = mw;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getSolutionAttribute() {
		return solutionAttribute;
	}
	public void setSolutionAttribute(String solutionAttribute) {
		this.solutionAttribute = solutionAttribute;
	}
	
}
