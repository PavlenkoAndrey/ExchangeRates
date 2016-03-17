package com.home.ratemvc.interfaces;

import java.util.ArrayList;
import java.util.TreeMap;

import com.home.ratemvc.exceptions.DataReceiverException;
import com.home.ratemvc.exceptions.InputDataException;
import com.home.ratemvc.objects.CurrencyRate;

public interface ICurrencyDataReceiver {
	public ArrayList<CurrencyRate> GetRatesForPeriod(String currencyID, String dateFrom, String dateTo) throws InputDataException, DataReceiverException;
	public TreeMap<String, String> getCurrenciesTable();
}
