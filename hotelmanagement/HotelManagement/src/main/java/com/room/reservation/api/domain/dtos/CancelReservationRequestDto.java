package com.room.reservation.api.domain.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CancelReservationRequestDto {

	
	@Positive(message="Enter Valid Booking Id")
	private int bookingId;
	
	@Min(value = 0, message = "AC rooms should be at least 0")
    @Max(value = 7, message = "AC rooms should be at most 7")
	private int acRooms;
	@Min(value = 0, message = "AC rooms should be at least 0")
    @Max(value = 6, message = "AC rooms should be at most 6")
	private int nonAcRooms;
	@Min(value = 0, message = "AC rooms should be at least 0")
    @Max(value = 8, message = "AC rooms should be at most 8")
	private int deluxRooms;
	
	
		
	
	
	
	
}
