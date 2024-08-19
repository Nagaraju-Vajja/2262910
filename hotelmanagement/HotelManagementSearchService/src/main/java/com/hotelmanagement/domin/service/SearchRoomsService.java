package com.hotelmanagement.domin.service;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.hotelmanagement.domin.dtos.SearchRoomsResponseDto;

public interface SearchRoomsService {
	
	public List<SearchRoomsResponseDto> getAvailableRooms(Date fromDate, Date toDate) throws ParseException, SQLException;


}
