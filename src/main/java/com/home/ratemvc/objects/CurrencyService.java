package com.home.ratemvc.objects;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static java.util.Arrays.asList;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.home.ratemvc.impls.SQLiteDAO;
import com.home.ratemvc.objects.Currency;


public class CurrencyService {
	
	static final String RUSSIAN_CURRENCY = "RUR";
	static private String sourceDailyXM    = "http://www.cbr.ru/scripts/XML_daily.asp";
	static private String sourceDynamicXMLPattern = "http://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=dateFrom&date_req2=dateTo&VAL_NM_RQ=curID";

	static private List<String> baseCurencies = asList("RUR", "USD", "EUR");
	static private HashMap<String, String> allCurenciesTable = new HashMap<String, String>();
	private static ArrayList<Currency> userCurrencies = new ArrayList<Currency>(); //(Arrays.asList(new Currency("USD", "Dollar USA"), new Currency("EUR", "European currency"), new Currency("RUR", "Russian ruble")));	
	static private LinkedHashSet<String> availableCurrenciesSet = new LinkedHashSet<String>();
	
	static SQLiteDAO sqLiteDAO;
	static {
		ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
		sqLiteDAO = (SQLiteDAO) context.getBean("sqliteDAO");
		//new SQLiteDAO().insertWithJDBC(currency);
		// Initialization allCurenciesTable 
		allCurenciesTable.put("RUR", "R01239");
		FillAllCurenciesTable();
		//FillCurrencyForDate();
		// Initialization availableCurrenciesTable. Contains allCurenciesTable without userCurrencies
	    //for(Map.Entry<String, String> entry : allCurenciesTable.entrySet()) {
	    //	availableCurrenciesSet.add(entry.getKey());
	    //}
		for(String code : allCurenciesTable.keySet() ) {
			availableCurrenciesSet.add(code);
		}
		for (Currency currency : userCurrencies) {
			availableCurrenciesSet.remove(currency.getCode());
		}
		List<Currency> list = sqLiteDAO.findAll2();
		for (Currency currency : list) {
			userCurrencies.add(currency);
			availableCurrenciesSet.remove(currency.getCode());
		}
		
	}
	

	public static LinkedHashSet<String> getAvailableCurrenciesSet() {
		return availableCurrenciesSet;
	}

	public static List<String> getBaseCurrencies() {
		return baseCurencies;
	}

	//static public GetAvailableCurrencies() {		
	//}
	
	private void LoadUserCurrencies() {
		
	}
	
	public List<Currency> getUserCurrencies() {
		return userCurrencies;
	}
	
	public ArrayList<String> getUserCurrenciesCodes() {
		ArrayList<String> list = new ArrayList<String>();
		for(Currency item : userCurrencies) {
			list.add(item.getCode());
		}
		return list;
	}
	
	public void AddCurrency(Currency currency) {
		if (!allCurenciesTable.containsKey(currency.getCode())) {
			return;
		}
		for (Currency cur: userCurrencies) {
			if (cur.getCode().equals(currency.getCode())) {
				cur.setDescription(currency.getDescription());
				sqLiteDAO.delete(currency.getCode());
				sqLiteDAO.insert(currency);
				return;
			}
		}
		userCurrencies.add(currency);
		sqLiteDAO.insert(currency);
		availableCurrenciesSet.remove(currency.getCode());
	}

	public void DeleteCurrency(String code) {
		Iterator<Currency> iter = userCurrencies.iterator();
		while (iter.hasNext()) {
			Currency currency = iter.next();
			if (currency.getCode().equals(code)) {
				iter.remove();
				sqLiteDAO.delete(code);
				availableCurrenciesSet.add(code);
				return;
			}
		}
	}
	
	public Currency getUserCurrencyByCode(String code) {
		Iterator<Currency> iter = userCurrencies.iterator();
		while (iter.hasNext()) {
			Currency currency = iter.next();
			if (currency.getCode().equals(code)) {
				return new Currency(currency.getCode(), currency.getDescription());
			}
		}
		return null;
	}
	
	public void Print() {
		for (Currency cur: userCurrencies) {
			System.out.println(cur.getCode() + "," + cur.getDescription() + "; ");
		}
		System.out.println();
	}
	
	public ArrayList<String> GetRatesForPeriod(String userCurrency, String baseCurrency, String dateFrom, String dateTo) {
		//
		ArrayList<CurrencyRate> userRates = GetRatesForPeriod(userCurrency, dateFrom, dateTo);
		ArrayList<CurrencyRate> baseRates = GetRatesForPeriod(baseCurrency, dateFrom, dateTo);
		ArrayList<CurrencyRate> arrayRates = (userRates != null) ? userRates : baseRates;
		if (arrayRates == null) {
			return null;
		}
		ArrayList<String> ratesUserToBase = new ArrayList<String>();
		for(int i = 0; i < arrayRates.size(); ++i) {
			double userValue = !userCurrency.equals(RUSSIAN_CURRENCY) ? Double.parseDouble(userRates.get(i).getValue()) : 1;  
			double baseValue = !baseCurrency.equals(RUSSIAN_CURRENCY) ? Double.parseDouble(baseRates.get(i).getValue()) : 1;
			double rate = userValue / baseValue;
			ratesUserToBase.add(String.format("%s   %.4f", arrayRates.get(i).getDate(), rate));
		}
		return ratesUserToBase;
	}
	
    //static private void FillRatesForPeriod(){	
	public ArrayList<CurrencyRate> GetRatesForPeriod(String currencyCode, String dateFrom, String dateTo) {
		if (!allCurenciesTable.containsKey(currencyCode)) {
			return null; 
		}
		System.out.println(currencyCode + ", " + allCurenciesTable.get(currencyCode));
		ArrayList<CurrencyRate> list = new ArrayList<CurrencyRate>();
		String sourceDynamicXML = sourceDynamicXMLPattern.replace("dateFrom",dateFrom).replace("dateTo", dateTo).replace("curID", allCurenciesTable.get(currencyCode));
		System.out.println(sourceDynamicXML);
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputStream stream = new URL(sourceDynamicXML).openStream();
          
            Document document = builder.parse(stream);
            Element valCurs = document.getDocumentElement();
            // System.out.println(valCurs.getAttribute("Date"));
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
                    //allCurenciesTable.put(currencyCode, currencyID);
                    //System.out.println(date + ": " + currencyRate);
                }
            }
            stream.close();
        } catch (Exception e){
            // e.printStackTrace();
        }
        return list;
    }
	
    static private void FillAllCurenciesTable(){
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputStream stream = new URL(sourceDailyXM).openStream();
          
            Document document = builder.parse(stream);
            Element valCurs = document.getDocumentElement();
            // System.out.println(valCurs.getAttribute("Date"));
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
                    allCurenciesTable.put(currencyCode, currencyID);
                    //System.out.println(currencyCode + ": " + currencyID);
                }
            }
            stream.close();
        } catch (Exception e){
            // e.printStackTrace();
        }
    }
	
}
