package com.room.reservation.api.domain.dtos;

import java.util.Date;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchRoomRequestDto {
	@NotNull(message="fromDate cannot be null")
	private Date fromDate;
	@NotNull(message="toDate cannot be null")
	private Date toDate;
	
	
	
}
