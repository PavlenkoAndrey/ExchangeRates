package com.home.ratemvc.models;

import com.home.ratemvc.objects.Currency;

public class HomeModel {
	Currency currency;
	Currency currencyEdit;
	String dateFrom; // private 
	String dateTo;
	String selectedBaseCurrency;
	String selectedUserCurrency;
	//String selectedAvailableCurrency;
	//String baseCurencies;
	
	public HomeModel() {
		currency = new Currency();
		currencyEdit = new Currency();
		dateFrom = "";
		dateTo   = "";
	}

	public HomeModel(String dateFrom, String dateTo) {
		currency = new Currency();
		this.dateFrom = dateFrom;
		this.dateTo   = dateTo;
	}
	
	public Currency getCurrencyEdit() {
		return currencyEdit;
	}

	public void setCurrencyEdit(Currency currencyEdit) {
		this.currencyEdit = currencyEdit;
	}

	public String getSelectedBaseCurrency() {
		return selectedBaseCurrency;
	}

	public void setSelectedBaseCurrency(String selected) {
		this.selectedBaseCurrency = selected;
	}
	
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	public String getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}
	public String getDateTo() {
		return dateTo;
	}
	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getSelectedUserCurrency() {
		return selectedUserCurrency;
	}

	public void setSelectedUserCurrency(String selectedUserCurrency) {
		this.selectedUserCurrency = selectedUserCurrency;
	}

}
