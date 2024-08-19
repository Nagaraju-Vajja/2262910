package com.hotelmanagement.domin.serviceimpl;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotelmanagement.domin.dtos.SearchRoomsResponseDto;
import com.hotelmanagement.domin.exceptions.ReservationException;
import com.hotelmanagement.domin.repositories.SearchRoomsRepo;
import com.hotelmanagement.domin.service.SearchRoomsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SearchRoomsServiceImple implements SearchRoomsService {

	@Autowired
	SearchRoomsRepo hotelroomsrep;

	
	
	
	@Override
	public List<SearchRoomsResponseDto> getAvailableRooms(Date fromDate, Date toDate) throws SQLException {

		log.info("method name: getAvailableRooms, request to getAvailableRooms from {} to {}  ", fromDate, toDate);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String fromDateStr = sdf.format(fromDate);
		String toDateStr = sdf.format(toDate);

		List<SearchRoomsResponseDto> response;
		
		Date fromDate1= setNoonTime(fromDate);
		Date toDate1= setElevenAMTime(toDate);
		log.info("{},{}",fromDate1, toDate);
		List<Integer> reservedRoomsInRangeOfDates = hotelroomsrep.getAllReservedRoomsInRangeOfFromDateToDate(fromDate1, toDate1);
		log.info("{}{}{}",reservedRoomsInRangeOfDates,fromDate1,toDate);
		if (reservedRoomsInRangeOfDates.isEmpty()) {

			int noOfAcRooms = hotelroomsrep.countRooms("AC");
			int noOfNonAcRooms = hotelroomsrep.countRooms("NON_AC");
			int noOfDeluxRooms = hotelroomsrep.countRooms("DELUX");
			response = availableRoomsInRangeOfFromDateToDate(noOfAcRooms, noOfNonAcRooms, noOfDeluxRooms);
			return response;
		}

		int reservedAcRooms = 0;
		int reservedNonAcRooms = 0;
		int reservedDeluxRooms = 0;

		List<Integer> AcRoomIds = hotelroomsrep.getRoomIds("AC");
		List<Integer> nonAcRoomIds = hotelroomsrep.getRoomIds("NON_AC");
		List<Integer> deluxRoomIds = hotelroomsrep.getRoomIds("DELUX");

		for (int each_room : reservedRoomsInRangeOfDates)

		{

			if (AcRoomIds.contains(each_room)) {
				reservedAcRooms += 1;
			} else if (nonAcRoomIds.contains(each_room)) {
				reservedNonAcRooms += 1;
			} else {
				reservedDeluxRooms += 1;
			}

		}
		if (AcRoomIds.size() + nonAcRoomIds.size() + deluxRoomIds.size() == reservedAcRooms + reservedNonAcRooms
				+ reservedDeluxRooms) {
			log.warn("method name:getAvailableRooms "+"No rooms available  from " + fromDateStr + " to " + toDateStr);
			throw new ReservationException("No rooms available  from " + fromDateStr + " to " + toDateStr);
		}
		int availableAcRooms = AcRoomIds.size() - reservedAcRooms;
		int availableNonAcRooms = nonAcRoomIds.size() - reservedNonAcRooms;
		int availableDeluxRooms = deluxRoomIds.size() - reservedDeluxRooms;

		List<SearchRoomsResponseDto> result = availableRoomsInRangeOfFromDateToDate(availableAcRooms,
				availableNonAcRooms, availableDeluxRooms);
		log.info("successfully returned list of available rooms...");
		return result;

	}

	public List<SearchRoomsResponseDto> availableRoomsInRangeOfFromDateToDate(int acRoomIds, int nonAcRoomIds,
			int deluxRoomIds) {

		log.info("Method Name:availableRoomsInRangeOfFromDateToDate  Running...");
		List<SearchRoomsResponseDto> available = new ArrayList<>();

		SearchRoomsResponseDto acRoomDto = new SearchRoomsResponseDto();
		acRoomDto.setRoomType("AC");
		acRoomDto.setPrice(1500);
		acRoomDto.setTotalAvailableRooms(acRoomIds);

		SearchRoomsResponseDto nonAcRoomDto = new SearchRoomsResponseDto();
		nonAcRoomDto.setRoomType("NON_AC");
		nonAcRoomDto.setPrice(1200);
		nonAcRoomDto.setTotalAvailableRooms(nonAcRoomIds);

		SearchRoomsResponseDto deluxRoomDto = new SearchRoomsResponseDto();
		deluxRoomDto.setRoomType("DELUX");
		deluxRoomDto.setPrice(2000);
		deluxRoomDto.setTotalAvailableRooms(deluxRoomIds);

		available.add(acRoomDto);
		available.add(nonAcRoomDto);
		available.add(deluxRoomDto);

		return available;
	}
	
	 Date setNoonTime(Date date) {
	        date.setHours(12);
	        date.setMinutes(0);
	        date.setSeconds(0);
	        return date;
	    }

Date setElevenAMTime(Date date) {
  date.setHours(11);
  date.setMinutes(0);
  date.setSeconds(0);
  return date;
}

}
