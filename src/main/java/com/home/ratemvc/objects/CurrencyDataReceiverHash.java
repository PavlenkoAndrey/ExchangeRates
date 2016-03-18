package com.home.ratemvc.objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import com.home.ratemvc.exceptions.DataReceiverException;
import com.home.ratemvc.exceptions.InputDataException;

public class CurrencyDataReceiverHash extends CurrencyDataReceiver {
	HashMap<String, HashMap<String, Double>> hashData = new HashMap<String, HashMap<String, Double>>();
	
	public ArrayList<CurrencyRate> GetRatesForPeriod(String currencyID, String dateFrom, String dateTo) throws DataReceiverException, InputDataException {
		try {
			if (!dateFrom.equals(dateTo) && CompareDates(dateFrom, dateTo) == false) {
				throw new InputDataException("DateTo should not be before DateFrom.");
			}
			HashMap<String, Double> hashMap = GetHashMapByCurrencyID(currencyID);
			ArrayList<CurrencyRate> ratesList = new ArrayList<CurrencyRate>();
			DateIterator dateIterator = new DateIterator(dateFrom, dateTo);
			
			FillRatesList:
			for(String date : dateIterator) {
				System.out.println(date);
				// Determine caching data and add caching data to ratesList
				while (hashMap.containsKey(date)) {
					Double value = hashMap.get(date);
					if (value != null) {
						ratesList.add(new CurrencyRate(date, value, true));
						System.out.println("added from cache " +  date + ": " + value);
					}
					if (!dateIterator.hasNext()) {
						break FillRatesList;
					}
					date = dateIterator.next();
				}
				// Determine data not cached
				String dateBegin = date;
				while (dateIterator.hasNext() && !hashMap.containsKey(dateIterator.getNext())) {
					date = dateIterator.next();
				}
				// Get data from server
				ArrayList<CurrencyRate> addToHashList = super.GetRatesForPeriod(currencyID, dateBegin, date);
				if (addToHashList != null && !addToHashList.isEmpty()) {
					// Add data to ratesList
					ratesList.addAll(addToHashList);
					for(CurrencyRate currency : addToHashList) {System.out.println("added from server " +  currency.toString());}
					// Add data to cache
					Iterator<CurrencyRate> itAddToHashList = addToHashList.iterator();
					CurrencyRate currencyRateCurrent = itAddToHashList.next();
					DateIterator dateIteratorHash = new DateIterator(dateBegin, date);
					for (String dateHash : dateIteratorHash) {
						// For days when data are not available (it can be the weekend or holidays) to add null to the cache
						Double value = null;
						if (dateHash.equals(currencyRateCurrent.getDate())) {
							value = currencyRateCurrent.getValue();
							if (itAddToHashList.hasNext()) {
								currencyRateCurrent = itAddToHashList.next();
							}
						}
						hashMap.put(dateHash, value);
						// System.out.println("cached " +  dateHash + ": " + value);					
					}
				}
			}
			return ratesList;
		}
		catch (ParseException ex) {
			throw new InputDataException("Incorrect input data format.");
		}
		catch (InputDataException | DataReceiverException ex ) {
			throw ex;
		}
		catch (Exception ex) {
			throw new DataReceiverException("");
		}
	}
	
	private HashMap<String, Double> GetHashMapByCurrencyID(String currencyID) {
		if (!hashData.containsKey(currencyID)) {
			hashData.put(currencyID, new HashMap<String, Double>());
		}
		return hashData.get(currencyID);
	}
	
	private class DateIterator implements Iterable<String>, Iterator<String> {
		private final Calendar calendar;
		private final SimpleDateFormat dateFormat;
		private final String dateEnd;
		private String nextDate;
		public DateIterator(String dateFrom, String dateTo) throws ParseException {
			dateFormat    = new SimpleDateFormat("dd.MM.yyyy");
			calendar      = Calendar.getInstance();
			calendar.setTime(dateFormat.parse(dateTo));
			calendar.add(Calendar.DATE, 1);
			this.dateEnd  = dateFormat.format(calendar.getTime());
			calendar.setTime(dateFormat.parse(dateFrom));
			nextDate = dateFormat.format(calendar.getTime());
		}
		@Override		
		public boolean hasNext() {
			return !nextDate.equals(dateEnd);
		}
		@Override		
		public String next() {
			String tempDate = dateFormat.format(calendar.getTime());
			calendar.add(Calendar.DATE, 1);
			nextDate = dateFormat.format(calendar.getTime());
			return tempDate;
		}
		public String getNext() {
			return nextDate;
		}
		@Override
		public Iterator<String> iterator() {
			return this;
		}
	}

	private boolean CompareDates(String firstDate, String secondDate) throws ParseException {
	    SimpleDateFormat dateFormat    = new SimpleDateFormat("dd.MM.yyyy");   
		Calendar calendar1 = Calendar.getInstance();    
		Calendar calendar2 = Calendar.getInstance();
		calendar1.setTime(dateFormat.parse(firstDate));
		calendar2.setTime(dateFormat.parse(secondDate));
		return calendar1.before(calendar2);
	}
}

