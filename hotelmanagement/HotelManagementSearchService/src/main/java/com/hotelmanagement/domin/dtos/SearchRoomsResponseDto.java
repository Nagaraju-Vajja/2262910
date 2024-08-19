package com.hotelmanagement.domin.dtos;



import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchRoomsResponseDto {

	private String roomType;
	private double price;
	private int totalAvailableRooms;

	

}
