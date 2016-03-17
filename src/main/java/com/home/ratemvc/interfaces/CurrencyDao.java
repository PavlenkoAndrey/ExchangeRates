package com.home.ratemvc.interfaces;

import java.util.List;

import com.home.ratemvc.objects.Currency;

public interface CurrencyDao {
	public void insert(Currency currency);
	public void delete(String code);
	public List<Currency> getCurrencyList();
}
