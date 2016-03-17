package com.home.ratemvc.models;

import javax.validation.constraints.Pattern;
import com.home.ratemvc.objects.Currency;
import com.home.ratemvc.objects.CurrencyDataReceiver;

public class HomeModel {
	private Currency currency;
	private Currency currencyEdit;
	@Pattern(regexp = CurrencyDataReceiver.regexpDatePattern, message = "Incorrect date format: dd.mm.yyyy")
	private String dateTo;
	@Pattern(regexp = CurrencyDataReceiver.regexpDatePattern, message = "Incorrect date format: dd.mm.yyyy")
	private String dateFrom; 
	private String selectedBaseCurrency;
	private String selectedUserCurrency;
	private String errorMessage;
	
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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}
