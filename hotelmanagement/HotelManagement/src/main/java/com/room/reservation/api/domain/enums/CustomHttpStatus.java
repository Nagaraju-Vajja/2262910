package com.room.reservation.api.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum CustomHttpStatus {

	Ok(200),
	BAD_REQUEST(400),
	INTERNAL_SERVER_ERROR(500);
	
	private  int code;
	
	
}
