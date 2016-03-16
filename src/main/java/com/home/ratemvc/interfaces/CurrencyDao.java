package com.home.ratemvc.interfaces;

import java.util.List;

import com.home.ratemvc.objects.Currency;

public interface CurrencyDao {

	void insert(Currency currency);

	void delete(String code);

	List<Currency> getCurrencyList();
}
