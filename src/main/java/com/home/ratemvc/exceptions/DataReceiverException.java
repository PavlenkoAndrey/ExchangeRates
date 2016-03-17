package com.home.ratemvc.exceptions;

public class DataReceiverException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DataReceiverException(String msg) {
		super ("Error receiving server data." + msg);
	}
	
	

}
