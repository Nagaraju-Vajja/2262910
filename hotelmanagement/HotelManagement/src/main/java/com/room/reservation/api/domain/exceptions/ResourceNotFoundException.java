package com.room.reservation.api.domain.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	HttpStatus status;
	 
	public ResourceNotFoundException(String message)
	{
		super(message);
		this.status=status;
	}
}
