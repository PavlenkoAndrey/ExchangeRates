package com.home.ratemvc.objects;
import static java.util.Arrays.asList;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;

import com.home.ratemvc.impls.SQLiteDAO;
import com.home.ratemvc.interfaces.ICurrencyDataReceiver;
import com.home.ratemvc.objects.Currency;

import com.home.ratemvc.exceptions.*;


public class CurrencyService {
	
	private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);
	
	// Data base to store user defined currencies
	public static SQLiteDAO sqLiteDAO;
	
	private static ICurrencyDataReceiver currencyDataReceiver = new CurrencyDataReceiverHash();
	
	// All existing currency is loaded at the beginning of the program
	private static final TreeMap<String, String> allCurenciesTable;
	// We can see the exchange rates relative to RUR, USD, EUR
	private static final List<String> baseCurrencies = asList("RUR", "USD", "EUR");
	
	// User defined currencies
	//private static ArrayList<Currency> userCurrencies = new ArrayList<Currency>();
	//static private HashMap<String, String> userCurrencies = new HashMap<String, String>();
	static private TreeMap<String, String> userCurrencies = new TreeMap<String, String>();
	// Currencies is not included in the userCurrencies list
	private static LinkedHashSet<String> availableCurrencies = new LinkedHashSet<String>();
	
	static {
		// Data base initialization
		ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
		try {
			sqLiteDAO = (SQLiteDAO) context.getBean("sqliteDAO");
		} catch(DataAccessException ex) {
			System.out.println("DataAccessException:" + ex.getMessage());
		} catch(Throwable ex) {
			System.out.println("Throwable:" + ex.getMessage());
		}
		
		// Collections initialization
		allCurenciesTable = currencyDataReceiver.getCurrenciesTable();

		for(String code : allCurenciesTable.keySet() ) {
			availableCurrencies.add(code);
		}
		
		List<Currency> list = sqLiteDAO.getCurrencyList();
		for (Currency currency : list) {
			userCurrencies.put(currency.getCode(), currency.getDescription());
			availableCurrencies.remove(currency.getCode());
		}
	}
	
	public ArrayList<String> GetRatesForPeriod(String userCurrency, String baseCurrency, String dateFrom, String dateTo) throws DataReceiverException, InputDataException {
		logger.info("GetRatesForPeriod: started");
		if (!baseCurrencies.contains(baseCurrency) || !userCurrencies.containsKey(userCurrency)
			||	!dateFrom.matches(CurrencyDataReceiver.regexpDatePattern) || !dateTo.matches(CurrencyDataReceiver.regexpDatePattern) ) { 
			logger.error("GetRatesForPeriod: Incorrect keys or date format.");
			throw new InputDataException("Incorrect keys or date format.");
		}
		String userCurrencyID = allCurenciesTable.get(userCurrency);
		String baseCurrencyID = allCurenciesTable.get(baseCurrency);

		ArrayList<CurrencyRate> userRates = currencyDataReceiver.GetRatesForPeriod(userCurrencyID, dateFrom, dateTo);
		ArrayList<CurrencyRate> baseRates = currencyDataReceiver.GetRatesForPeriod(baseCurrencyID, dateFrom, dateTo);
		if ((userRates == null) || (baseRates == null) || userRates.isEmpty() || (userRates.size() != baseRates.size())) {
			logger.error("GetRatesForPeriod: Incorrect rates data received from the server");
			return null;
		}
		ArrayList<String> ratesUserToBase = new ArrayList<String>();
		for(int i = 0; i < baseRates.size(); ++i) {
			double userValue = userRates.get(i).getValue();  
			double baseValue = baseRates.get(i).getValue();
			if (baseValue == 0) {
				logger.error("GetRatesForPeriod: Some server data are zero." );
				throw new DataReceiverException("Some server data are zero.");
			}
			double rate = userValue / baseValue;
			String isUserDataCached = userRates.get(i).isCached() ? "*" : "  ";
			String isBaseDataCached = baseRates.get(i).isCached() ? "*" : "  ";
			ratesUserToBase.add(String.format("%s:   %.4f %s%s", baseRates.get(i).getDate(), rate, isUserDataCached, isBaseDataCached));
		}
		return ratesUserToBase;
	}
	
	public static List<String> getBaseCurrencies() {
		return baseCurrencies;
	}

	public Set<Entry<String, String>> getUserCurrenciesEntries() {
		return userCurrencies.entrySet();
	}
	
	public LinkedHashSet<String> getAvailableCurrencies() {
		return availableCurrencies;
	}

	public Set<String> getUserCurrenciesCodes() {
		return userCurrencies.keySet();
	}
	
	
	
	public static TreeMap<String, String> getUserCurrencies() {
		return userCurrencies;
	}

	public String GetUserCurrencyDescription(String code) {
		return userCurrencies.get(code);
	}
	
	public void AddCurrency(Currency currency) throws InputDataException {
		if (!allCurenciesTable.containsKey(currency.getCode())) {
			throw new InputDataException("Incorrect currency code.");
		}
		if (userCurrencies.containsKey(currency.getCode())) {
			userCurrencies.put(currency.getCode(), currency.getDescription());
			sqLiteDAO.delete(currency.getCode());
			sqLiteDAO.insert(currency);
			
		} else {
			userCurrencies.put(currency.getCode(), currency.getDescription());
			sqLiteDAO.insert(currency);
			availableCurrencies.remove(currency.getCode());
		}
	}

	public void DeleteCurrency(String code) throws InputDataException {
		if (!userCurrencies.containsKey(code)) {
			throw new InputDataException("Incorrect currency code");
		}
		userCurrencies.remove(code);
		sqLiteDAO.delete(code);
		availableCurrencies.add(code);
	}

}
