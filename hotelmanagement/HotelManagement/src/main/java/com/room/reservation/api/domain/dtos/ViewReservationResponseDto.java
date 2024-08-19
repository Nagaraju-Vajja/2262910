package com.room.reservation.api.domain.dtos;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ViewReservationResponseDto {

	private String checkInDate;
	private String checkOutDate;
	private int noOfRooms;
	private int acRooms;
	private int nonAcRooms;

	private int deluxRooms;
	private double pricePaid;

	

	

}
