package com.home.ratemvc.objects;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.home.ratemvc.exceptions.DataReceiverException;
import com.home.ratemvc.exceptions.InputDataException;
import com.home.ratemvc.interfaces.ICurrencyDataReceiver;

public class CurrencyDataReceiver implements ICurrencyDataReceiver {
	private static final String sourceDailyXM           = "http://www.cbr.ru/scripts/XML_daily.asp";
	private static final String sourceDynamicXMLPattern = "http://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=dateFrom&date_req2=dateTo&VAL_NM_RQ=curID";
	public  static final String regexpDatePattern       = "^(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[012])\\.[0-9]{4}$"; // dd.mm.yyyy
	private static final String RUSSIAN_CURRENCY    = "RUR";
	private static final String RUSSIAN_CURRENCY_ID  = "RUR_ID";
	private static final String EUROPEAN_CURRENCY_ID = "R01239";
	
	public ArrayList<CurrencyRate> GetRatesForPeriod(String currencyID, String dateFrom, String dateTo) throws InputDataException, DataReceiverException {
		ArrayList<CurrencyRate> list = new ArrayList<CurrencyRate>();
		String modCurrencyID = currencyID.equals(RUSSIAN_CURRENCY_ID) ? EUROPEAN_CURRENCY_ID : currencyID;
		String sourceDynamicXML = sourceDynamicXMLPattern.replace("dateFrom",dateFrom).replace("dateTo", dateTo).replace("curID", modCurrencyID);
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
                    double currencyRateDouble = 1;
                    // Get currency rate, currencyRate = 1 for russian currency
                    if (!currencyID.equals(RUSSIAN_CURRENCY_ID)) {
                    	String currencyRate = "";
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
                    	currencyRateDouble = Double.parseDouble(currencyRate);
                    }
                    list.add(new CurrencyRate(date, currencyRateDouble));
                 }
            }
            stream.close();
        } catch (Exception e){
            throw new DataReceiverException("");
        }
        return list;
    }
	
    public TreeMap<String, String> getCurrenciesTable(){
    	TreeMap<String, String> allCurrencies = new TreeMap<String, String>();

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
    	allCurrencies.put(RUSSIAN_CURRENCY, RUSSIAN_CURRENCY_ID);    	
        return allCurrencies;
    }


}
