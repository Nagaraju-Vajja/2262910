package com.room.reservation.api.domain.dtos;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomsReservationRequestDto {

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "Please Enter Checkin Date")
	private Date checkInDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "Please Enter Checkout Date")
	private Date checkOutDate;
	
	
	

	@Min(value = 0, message = "AC rooms should be at least 0")
	@Max(value = 7, message = "AC rooms should be at most 7")
	private int noOfAcRooms;

	@Min(value = 0, message = "Non-AC rooms should be at least 0")
	@Max(value = 6, message = "Non-AC rooms should be at most 6")
	private int noOfNonAcRooms;

	@Min(value = 0, message = "Delux rooms should be at least 0")
	@Max(value = 8, message = "Delux rooms should be at most 8")
	private int noOfDeluxRooms;

	

	

}
