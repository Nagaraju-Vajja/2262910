package com.room.reservation.api.domain.services;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.room.reservation.api.domain.dtos.SearchRoomsResponseDto;

public interface HotelRoomsService {

	public List<SearchRoomsResponseDto> getAvailableRooms(Date fromDate, Date toDate) throws ParseException, SQLException;
	
	 
}
