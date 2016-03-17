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
			if (CompareDates(dateFrom, dateTo) == false) {
				throw new InputDataException("DateTo should be after DateFrom.");
			}
			HashMap<String, Double> hashMap = GetHashMapByCurrencyID(currencyID);
			ArrayList<CurrencyRate> ratesList = new ArrayList<CurrencyRate>();
			DateIterator dateIterator = new DateIterator(dateFrom, dateTo);
			
			for(String date : dateIterator) {
				System.out.println(date);
				// Determine caching data and add caching data to ratesList
				dateIterator.prev();
				while (dateIterator.hasNext() && hashMap.containsKey(date = dateIterator.next())) {
					Double value = hashMap.get(date);
					if (value != null) {
						ratesList.add(new CurrencyRate(date, value, true));
						System.out.println("added from cache " +  date + ": " + value);
					}
				}
				if (!dateIterator.hasNext()) {
					break;
				}
				// Determine data not cached
				String dateBegin = date;
				while (dateIterator.hasNext() && !hashMap.containsKey(date)) {
					date = dateIterator.next();
				}
				if (dateIterator.hasNext()) {
					date = dateIterator.prev();
				}
				String dateLast = date;
				String dateEnd = dateIterator.next();
				
				// Get data from server
				ArrayList<CurrencyRate> tempList = super.GetRatesForPeriod(currencyID, dateBegin, dateLast);
				if (tempList != null && !tempList.isEmpty()) {
					// Add data to ratesList
					ratesList.addAll(tempList);
					for(CurrencyRate currency : tempList) {System.out.println("added from server " +  currency.toString());}
					// Add data to cache
					int i = 0;
					for(date = dateIterator.set(dateBegin); !date.equals(dateEnd) && (i < tempList.size()); date = dateIterator.next()) {
						Double value = date.equals(tempList.get(i).getDate()) ? tempList.get(i++).getValue() : null;
						hashMap.put(date, value);
						System.out.println("cached " +  date + ":" + value);					
					}
				}
				date = dateIterator.set(dateLast);
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
		private final String dateTo; // date
		private String currentDate;
		public DateIterator(String dateFrom, String dateTo) throws ParseException {
			this.dateTo   = dateTo;
			dateFormat    = new SimpleDateFormat("dd.MM.yyyy");   
			calendar      = Calendar.getInstance();    
			calendar.setTime(dateFormat.parse(dateFrom));
			calendar.add(Calendar.DATE, -1);
			currentDate = dateFormat.format(calendar.getTime());
		}
		@Override		
		public boolean hasNext() {
			return !currentDate.equals(dateTo);
		}
		@Override		
		public String next() {
			calendar.add(Calendar.DATE, 1);
			return currentDate = dateFormat.format(calendar.getTime());
		}
		public String prev() {
			calendar.add(Calendar.DATE, -1);
			return currentDate = dateFormat.format(calendar.getTime());
		}
		@Override
		public Iterator<String> iterator() {
			return this;
		}
		public String set(String date) throws ParseException {
			calendar.setTime(dateFormat.parse(date));
			return currentDate = dateFormat.format(calendar.getTime());
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

