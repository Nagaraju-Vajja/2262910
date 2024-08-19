package com.hotelmanagement.domin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum CustomHttpStatus {

	Ok(200),
	BAD_REQUEST(400),
	INTERNAL_SERVER_ERROR(500);
	
	private  int code;
	
	
}
