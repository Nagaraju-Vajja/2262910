package com.room.reservation.api.domain.servicempl;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.room.reservation.api.domain.dtos.SearchRoomsResponseDto;
import com.room.reservation.api.domain.exceptions.ReservationException;
import com.room.reservation.api.domain.repositories.HotelRoomsRepo;
import com.room.reservation.api.domain.repositories.RoomDetailsRepo;
import com.room.reservation.api.domain.repositories.ReservationDetailsRepo;
import com.room.reservation.api.domain.services.HotelRoomsService;
import com.room.reservation.api.domain.services.ReservationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HotelRoomsServiceImpl implements HotelRoomsService {

	@Autowired
	HotelRoomsRepo hotelroomsrepository;

	@Autowired
	ReservationService bookingdetailsservice;

	@Autowired
	ReservationDetailsRepo bookingdetails;
	

	@Autowired
	RoomDetailsRepo roomdetailsrepo;

	@Override
	public List<SearchRoomsResponseDto> getAvailableRooms(Date fromDate, Date toDate) throws SQLException {
		
		log.info("method name: getAvailableRooms, request to getAvailableRooms from {} to {}  ",fromDate, toDate);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String fromDateStr = sdf.format(fromDate);
		String toDateStr = sdf.format(toDate);

		List<SearchRoomsResponseDto> response;

		List<Integer> reservedRoomsInRangeOfDates = roomdetailsrepo.getAllReservedRoomsInRangeOfFromDateToDate(fromDate,
				toDate);

		if (reservedRoomsInRangeOfDates.isEmpty()) {

			int noOfAcRooms = hotelroomsrepository.countRooms("AC");
			int noOfNonAcRooms = hotelroomsrepository.countRooms("NON_AC");
			int noOfDeluxRooms = hotelroomsrepository.countRooms("DELUX");
			response = availableRoomsInRangeOfFromDateToDate(noOfAcRooms, noOfNonAcRooms, noOfDeluxRooms);
			return response;
		}

		int reservedAcRooms = 0;
		int reservedNonAcRooms = 0;
		int reservedDeluxRooms = 0;

		List<Integer> AcRoomIds = hotelroomsrepository.getRoomIds("AC");
		List<Integer> nonAcRoomIds = hotelroomsrepository.getRoomIds("NON_AC");
		List<Integer> deluxRoomIds = hotelroomsrepository.getRoomIds("DELUx");

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
			log.warn("method name:getAvailableRooms,"+" No rooms available  from " + fromDateStr +  " to "  + toDateStr);
			throw new ReservationException("No rooms available  from " + fromDateStr + " to " + toDateStr);
		}
		int availableAcRooms = AcRoomIds.size() - reservedAcRooms;
		int availableNonAcRooms = nonAcRoomIds.size() - reservedNonAcRooms;
		int availableDeluxRooms = deluxRoomIds.size() - reservedDeluxRooms;

		List<SearchRoomsResponseDto> result = availableRoomsInRangeOfFromDateToDate(availableAcRooms,
				availableNonAcRooms, availableDeluxRooms);
		return result;

	}

	public List<SearchRoomsResponseDto> availableRoomsInRangeOfFromDateToDate(int acRoomIds, int nonAcRoomIds,
			int deluxRoomIds) {
		log.info("Method Name: availableRoomsInRangeOfFromDateToDate,requested from method getAvailableRooms ");
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
}
