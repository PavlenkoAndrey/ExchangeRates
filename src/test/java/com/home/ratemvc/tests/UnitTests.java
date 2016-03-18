package com.home.ratemvc.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import com.home.ratemvc.interfaces.ICurrencyDataReceiver;
import com.home.ratemvc.objects.CurrencyDataReceiver;
import com.home.ratemvc.objects.CurrencyDataReceiverHash;
import com.home.ratemvc.objects.CurrencyRate;


public class UnitTests {
	
	@Test
	public void TestCurrencyDataReceiverHash() {
		ICurrencyDataReceiver currencyDataReceiver = new CurrencyDataReceiver();
		ICurrencyDataReceiver currencyDataReceiverHash = new CurrencyDataReceiverHash();
		
		String userCurrencyID =  "R01239"; // EUR
		String dateFrom = "25.02.2016";
		String dateTo   = "05.03.2016";
		
		try {
			ArrayList<CurrencyRate> userRates = currencyDataReceiver.GetRatesForPeriod(userCurrencyID, dateFrom, dateTo);
			ArrayList<CurrencyRate> userRatesTemp;
			userRatesTemp =  currencyDataReceiverHash.GetRatesForPeriod(userCurrencyID, "01.03.2016", "05.03.2016");
			userRatesTemp =  currencyDataReceiverHash.GetRatesForPeriod(userCurrencyID, "26.02.2016", dateTo);
			userRatesTemp =  currencyDataReceiverHash.GetRatesForPeriod(userCurrencyID, "26.02.2016", dateTo);
			//			
			ArrayList<CurrencyRate> userRatesHash1 = currencyDataReceiverHash.GetRatesForPeriod(userCurrencyID, dateFrom, dateTo);
			//ArrayList<CurrencyRate> userRatesHash2 = currencyDataReceiverHash.GetRatesForPeriod(userCurrencyID, dateFrom, dateTo);
			System.out.println(userRates);
			System.out.println(userRatesHash1);
			assertEquals(userRates.size(), userRatesHash1.size());
			for(int i = 0; i < userRates.size(); ++i) {
				CurrencyRate currency1 = userRates.get(i);
				CurrencyRate currency2 = userRatesHash1.get(i);
				//CurrencyRate currency3 = userRatesHash2.get(i);
				assert(currency1.getDate().equals(currency2.getDate()));
				assert(currency1.getValue() == currency2.getValue());
			}
		}
		catch (Exception ex) {
			assert(false);
		}
	}	
	
	@Test
	public void TestCurrencyDataReceiverHash1() {
		ICurrencyDataReceiver currencyDataReceiver = new CurrencyDataReceiver();
		ICurrencyDataReceiver currencyDataReceiverHash = new CurrencyDataReceiverHash();
		
		String userCurrencyID =  "R01239"; // EUR
		String dateFrom = "25.12.2015";
		String dateTo   = "15.03.2016";
		
		try {
			ArrayList<CurrencyRate> userRates = currencyDataReceiver.GetRatesForPeriod(userCurrencyID, dateFrom, dateTo);
			ArrayList<CurrencyRate> userRatesTemp;
			userRatesTemp =  currencyDataReceiverHash.GetRatesForPeriod(userCurrencyID, "30.12.2015", "31.12.2015");
			userRatesTemp =  currencyDataReceiverHash.GetRatesForPeriod(userCurrencyID, "05.01.2016", "15.01.2016");
			userRatesTemp =  currencyDataReceiverHash.GetRatesForPeriod(userCurrencyID, "08.02.2016", "15.02.2016");
			userRatesTemp =  currencyDataReceiverHash.GetRatesForPeriod(userCurrencyID, "20.02.2016", "25.02.2016");
			userRatesTemp =  currencyDataReceiverHash.GetRatesForPeriod(userCurrencyID, "18.02.2016", "22.02.2016");
			userRatesTemp =  currencyDataReceiverHash.GetRatesForPeriod(userCurrencyID, "25.02.2016", "27.02.2016");
			userRatesTemp =  currencyDataReceiverHash.GetRatesForPeriod(userCurrencyID, "01.03.2016", "05.03.2016");
			ArrayList<CurrencyRate> userRatesHash1 = currencyDataReceiverHash.GetRatesForPeriod(userCurrencyID, dateFrom, dateTo);
			ArrayList<CurrencyRate> userRatesHash2 = currencyDataReceiverHash.GetRatesForPeriod(userCurrencyID, dateFrom, dateTo);
			for(int i = 0; i < userRates.size(); ++i) {
				CurrencyRate currency1 = userRates.get(i);
				CurrencyRate currency2 = userRatesHash1.get(i);
				CurrencyRate currency3 = userRatesHash2.get(i);
				assert(currency1.getDate().equals(currency2.getDate()) || currency1.getDate().equals(currency3.getDate()));
				assert(currency1.getValue() == currency2.getValue() || currency1.getValue() == currency3.getValue());
			}
			System.out.println(userRates);
		}
		catch (Exception ex) {
			assert(false);
		}
	}	
	
}
