package com.hotelmanagement.domin.exceptions;




import com.hotelmanagement.domin.enums.CustomHttpStatus;

import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class ValidationException extends RuntimeException{
		
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private CustomHttpStatus customHttpStatus;

	public ValidationException(String message, CustomHttpStatus customHttpStatus)
	{
		super(message);
		this.customHttpStatus=customHttpStatus;
		
	}
}





