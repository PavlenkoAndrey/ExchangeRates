package com.home.ratemvc.models;

import javax.validation.constraints.Pattern;
import com.home.ratemvc.objects.Currency;

public class HomeModel {
	private Currency currency;
	private Currency currencyEdit;
	private String dateTo;
	@Pattern(regexp = "^(0[1-9]|1[012])\\.(0[1-9]|1[0-9]|2[0-9]|3[01])\\.[0-9]{4}$", message = "[Correct format date dd.mm.yyyy]")
	private String dateFrom; 
	@Pattern(regexp = "^(0[1-9]|1[012])\\.(0[1-9]|1[0-9]|2[0-9]|3[01])\\.[0-9]{4}$", message = "[Correct format date dd.mm.yyyy]")
	private String selectedBaseCurrency;
	private String selectedUserCurrency;
	private boolean cacheData;
	
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

	public boolean isCacheData() {
		return cacheData;
	}

	public void setCacheData(boolean cacheData) {
		this.cacheData = cacheData;
	}
	
	

}
