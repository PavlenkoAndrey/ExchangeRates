package com.home.ratemvc.exceptions;

public class InputDataException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InputDataException(String msg) {
		super("Input data error." + msg);
	}

}
