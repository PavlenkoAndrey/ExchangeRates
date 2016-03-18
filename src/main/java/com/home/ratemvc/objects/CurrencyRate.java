package com.home.ratemvc.objects;

import java.util.Locale;

public class CurrencyRate {
	private String date;
	private double value;
	private boolean cached;
	
	CurrencyRate(String date, double value) {
		this.date = date;
		this.value = value;
		this.cached = false;
	}

	CurrencyRate(String date, double value, boolean cached) {
		this.date = date;
		this.value = value;
		this.cached = cached;
	}
	
	
	public String toString() {
		String scached = cached?"*":" ";
		return date + scached + ":" + String.format(new Locale("ru") ,"%4f", value) + scached;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}

	public boolean isCached() {
		return cached;
	}

	public void setCached(boolean cached) {
		this.cached = cached;
	}
	
	

}