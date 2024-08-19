package com.hotelmanagement.domin.exceptions;

import lombok.Getter;
import lombok.Setter;




@Getter
@Setter
public class ReservationException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	

	public ReservationException(String message)
	{
		super(message);
		
	}
	
	
}
