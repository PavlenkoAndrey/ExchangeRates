package com.home.ratemvc.objects;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import static java.util.Arrays.asList;
import java.io.InputStream;
import java.net.URL;
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
import com.home.ratemvc.objects.Currency;


public class CurrencyService {
	
	private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);
	
	private static final String RUSSIAN_CURRENCY = "RUR";
	private static String sourceDailyXM    = "http://www.cbr.ru/scripts/XML_daily.asp";
	private static String sourceDynamicXMLPattern = "http://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=dateFrom&date_req2=dateTo&VAL_NM_RQ=curID";

	// Data base to store user defined currencies
	public static SQLiteDAO sqLiteDAO;
	
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
		allCurenciesTable = AllCurrenciesTableInitialization();

		for(String code : allCurenciesTable.keySet() ) {
			availableCurrencies.add(code);
		}
		
		List<Currency> list = sqLiteDAO.getCurrencyList();
		for (Currency currency : list) {
			userCurrencies.put(currency.getCode(), currency.getDescription());
			availableCurrencies.remove(currency.getCode());
		}
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
	
	public String GetUserCurrencyDescription(String code) {
		return userCurrencies.get(code);
	}
	
	public void AddCurrency(Currency currency) {
		if (!allCurenciesTable.containsKey(currency.getCode())) {
			return;
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

	public void DeleteCurrency(String code) {
		if (userCurrencies.containsKey(code)) {
			userCurrencies.remove(code);
			sqLiteDAO.delete(code);
			availableCurrencies.add(code);
		}
	}

	public ArrayList<String> GetRatesForPeriod(String userCurrency, String baseCurrency, String dateFrom, String dateTo) {
		//
		if (!baseCurrencies.contains(baseCurrency) || !userCurrencies.containsKey(userCurrency)) {
			return null;
		}

		ArrayList<CurrencyRate> userRates = GetRatesForPeriod(userCurrency, dateFrom, dateTo);
		ArrayList<CurrencyRate> baseRates = GetRatesForPeriod(baseCurrency, dateFrom, dateTo);
		ArrayList<String> ratesUserToBase = new ArrayList<String>();
		for(int i = 0; i < baseRates.size(); ++i) {
			double userValue = !userCurrency.equals(RUSSIAN_CURRENCY) ? Double.parseDouble(userRates.get(i).getValue()) : 1;  
			double baseValue = !baseCurrency.equals(RUSSIAN_CURRENCY) ? Double.parseDouble(baseRates.get(i).getValue()) : 1;
			double rate = userValue / baseValue;
			ratesUserToBase.add(String.format("%s:   %.4f", baseRates.get(i).getDate(), rate));
		}
		return ratesUserToBase;
	}
	
	public ArrayList<CurrencyRate> GetRatesForPeriod(String currencyCode, String dateFrom, String dateTo) {
		if (!allCurenciesTable.containsKey(currencyCode)) {
			return null; 
		}
		logger.debug("GetRatesForPeriod: " + currencyCode + ", " + allCurenciesTable.get(currencyCode));
		ArrayList<CurrencyRate> list = new ArrayList<CurrencyRate>();
		String sourceDynamicXML = sourceDynamicXMLPattern.replace("dateFrom",dateFrom).replace("dateTo", dateTo).replace("curID", allCurenciesTable.get(currencyCode));
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputStream stream = new URL(sourceDynamicXML).openStream();
          
            Document document = builder.parse(stream);
            Element valCurs = document.getDocumentElement();
            NodeList valuteList = valCurs.getChildNodes();
            for(int i=0; i < valuteList.getLength(); ++i) {
                Node valute = valuteList.item(i);
                if(valute instanceof Element){
                    Element valuteElement = (Element)valute;
                    String date = valuteElement.getAttribute("Date");
                    String currencyRate = "";
                    // Get currency rate
                    NodeList valuteElementList = valuteElement.getChildNodes();
                    for(int j=0; j < valuteElementList.getLength(); ++j) {
                        Node valuteProperty = valuteElementList.item(j);
                        if(valuteProperty instanceof Element){
                            Element property = (Element)valuteProperty;
                            if (property.getTagName().equals("Value")) {
                            	Text textNode = (Text)property.getFirstChild();
                            	currencyRate = textNode.getData().trim().replaceAll(",", ".");
                            }
                        }
                    }
                    list.add(new CurrencyRate(date, currencyRate));
                 }
            }
            stream.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
	
    static private TreeMap<String, String> AllCurrenciesTableInitialization(){
    	TreeMap<String, String> allCurrencies = new TreeMap<String, String>();
    	allCurrencies.put(RUSSIAN_CURRENCY, "R01239"); // EUR

    	try (InputStream stream = new URL(sourceDailyXM).openStream();)
        {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
          
            Document document = builder.parse(stream);
            Element valCurs = document.getDocumentElement();
             NodeList valuteList = valCurs.getChildNodes();
            for(int i=0; i < valuteList.getLength(); ++i) {
                Node valute = valuteList.item(i);
                if(valute instanceof Element){
                    Element valuteElement = (Element)valute;
                    String currencyID = valuteElement.getAttribute("ID");
                    String currencyCode = "";
                    // Get currencyCode	
                    NodeList valuteElementList = valuteElement.getChildNodes();
                    for(int j=0; j < valuteElementList.getLength(); ++j) {
                        Node valuteProperty = valuteElementList.item(j);
                        if(valuteProperty instanceof Element){
                            Element property = (Element)valuteProperty;
                            if (property.getTagName().equals("CharCode")) {
                            	Text textNode = (Text)property.getFirstChild();
                            	currencyCode = textNode.getData().trim();
                            }
                        }
                    }
                    allCurrencies.put(currencyCode, currencyID);
                 }
            }
            stream.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return allCurrencies;
    }
	
}
