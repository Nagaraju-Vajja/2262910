package com.room.reservation.api.domain.servicempl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.room.reservation.api.domain.dtos.HeadersDto;
import com.room.reservation.api.domain.dtos.RoomsReservartionResponseDto;
import com.room.reservation.api.domain.dtos.RoomsReservationRequestDto;
import com.room.reservation.api.domain.entities.ReservationDetails;
import com.room.reservation.api.domain.entities.RoomDetails;
import com.room.reservation.api.domain.enums.CustomHttpStatus;
import com.room.reservation.api.domain.exceptions.ReservationException;
import com.room.reservation.api.domain.exceptions.ValidationException;
import com.room.reservation.api.domain.kafka.KafkaProducer;
import com.room.reservation.api.domain.repositories.HotelRoomsRepo;
import com.room.reservation.api.domain.repositories.ReservationDetailsRepo;
import com.room.reservation.api.domain.repositories.RoomDetailsRepo;
import com.room.reservation.api.domain.services.ReservationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReservationServiceImpl implements ReservationService {

	@Autowired
	ReservationDetailsRepo reservationDetailsRepo;

	@Autowired
	HotelRoomsRepo hotelRoomsRepo;

	@Autowired
	RoomDetailsRepo roomDetailsRepo;

	@Autowired
	KafkaProducer kafkaProducer;

	RoomsReservartionResponseDto result = new RoomsReservartionResponseDto();

	@Override
	public RoomsReservartionResponseDto reserveHotelRooms(RoomsReservationRequestDto bookingdetails) {

		log.info(" method name:saveReservation, recived request to reserve rooms  ");

		if (bookingdetails.getNoOfAcRooms() + bookingdetails.getNoOfNonAcRooms()
				+ bookingdetails.getNoOfDeluxRooms() == 0) {
			
			log.error("Method Name:saveReservation, please enter required details for reservation");
			throw new ValidationException("please enter required details for reservation",
					CustomHttpStatus.BAD_REQUEST);
		}
		List<Integer> acRooms = hotelRoomsRepo.getRoomIds("AC");
		List<Integer> nonAcRooms = hotelRoomsRepo.getRoomIds("NON_AC");
		List<Integer> deluxRooms = hotelRoomsRepo.getRoomIds("DELUX");

		List<Integer> tempAcrooms = new ArrayList<>(acRooms);
		List<Integer> tempNonAcRooms = new ArrayList<>(nonAcRooms);
		List<Integer> tempDeluxRooms = new ArrayList<>(deluxRooms);

		Date newCheckIn = setNoonTime(bookingdetails.getCheckInDate());
		Date newCheckOut = setElevenAMTime(bookingdetails.getCheckOutDate());

//   getting all reserved rooms in range of fromDate and toDate
		List<Integer> reservedRoomsInRangeOfDates = roomDetailsRepo
				.getAllReservedRoomsInRangeOfFromDateToDate(newCheckIn, newCheckOut);
		
		if (reservedRoomsInRangeOfDates.isEmpty()) {
			// selecting rooms from all available rooms
			String message = selectRooms(acRooms, nonAcRooms, deluxRooms, bookingdetails);

			result.setMessage(message);

			kafkaProducer.sendMessage(message);
			return result;
		}
		

		Set<Integer> reservedAcRooms = new HashSet<>();
		Set<Integer> reservedNonAcrooms = new HashSet<>();
		Set<Integer> reservedDeluxRooms = new HashSet<>();

		for (int eachRoom : reservedRoomsInRangeOfDates)

		{
			if (acRooms.contains(eachRoom)) {
				reservedAcRooms.add(eachRoom);
			} else if (nonAcRooms.contains(eachRoom)) {
				reservedNonAcrooms.add(eachRoom);
			} else {
				reservedDeluxRooms.add(eachRoom);
			}
		}

		tempAcrooms.removeAll(reservedAcRooms);
		tempNonAcRooms.removeAll(reservedNonAcrooms);
		tempDeluxRooms.removeAll(reservedDeluxRooms);

// validating whether requested number of rooms available or not
		validateReservations(tempAcrooms, tempNonAcRooms, tempDeluxRooms, reservedAcRooms, reservedNonAcrooms,
				reservedDeluxRooms, bookingdetails);
// selecting rooms for reservation after finding available rooms
		String message = selectRooms(tempAcrooms, tempNonAcRooms, tempDeluxRooms, bookingdetails);

		result.setMessage(message);
		log.info(message+"qqqqqqqqqqqqqqqqqqqqqqqqqqqqq");	
		kafkaProducer.sendMessage(message);
		return result;

	}
	

	public String selectRooms(List<Integer> acRooms, List<Integer> nonAcRooms, List<Integer> deluxRooms,
			RoomsReservationRequestDto bookingdetails) {
		log.info(" method name: selectRooms requested from saveReservation method");

		List<Integer> selectedAcRooms = new ArrayList<>();
		List<Integer> selectedNonAcRooms = new ArrayList<>();
		List<Integer> selectedDeluxRooms = new ArrayList<>();

		if (bookingdetails.getNoOfAcRooms() != 0 && bookingdetails.getNoOfAcRooms() <= acRooms.size()) {
			int noofrooms = bookingdetails.getNoOfAcRooms();
			selectedAcRooms = acRooms.subList(0, noofrooms);

		}
		if (bookingdetails.getNoOfNonAcRooms() != 0 && bookingdetails.getNoOfNonAcRooms() <= nonAcRooms.size()) {
			int noofrooms = bookingdetails.getNoOfNonAcRooms();
			selectedNonAcRooms = nonAcRooms.subList(0, noofrooms);

		}
		if (bookingdetails.getNoOfDeluxRooms() != 0 && bookingdetails.getNoOfDeluxRooms() <= deluxRooms.size()) {
			int noofrooms = bookingdetails.getNoOfDeluxRooms();
			selectedDeluxRooms = deluxRooms.subList(0, noofrooms);

		}

		List<Integer> combinedlist = new ArrayList<>();

		if (!selectedAcRooms.isEmpty())
			combinedlist.addAll(selectedAcRooms);

		if (!selectedNonAcRooms.isEmpty())
			combinedlist.addAll(selectedNonAcRooms);

		if (!selectedDeluxRooms.isEmpty())
			combinedlist.addAll(selectedDeluxRooms);

		int bookingid = saveReservationRooms(bookingdetails, selectedAcRooms, selectedNonAcRooms, selectedDeluxRooms,
				combinedlist);

		log.info("rooms reserved successfully with booking Id: " + bookingid);

		return "rooms reserved successfully with booking Id: " + bookingid;
	}

	public int saveReservationRooms(RoomsReservationRequestDto bookingdetails, List<Integer> selectedAcRooms,
			List<Integer> selectedNonAcRooms, List<Integer> selectedDeluxRooms, List<Integer> combinedlist) {

		log.info(" method name: saveReservationRooms, requested from method select rooms");
		ReservationDetails bookings = new ReservationDetails();

		int totalrooms = bookingdetails.getNoOfAcRooms() + bookingdetails.getNoOfNonAcRooms()
				+ bookingdetails.getNoOfDeluxRooms();
		
		Date newCheckin = setNoonTime(bookingdetails.getCheckInDate());
		Date newCheckOut = setElevenAMTime(bookingdetails.getCheckOutDate());
		
		bookings.setCheckInDate(newCheckin);
		bookings.setCheckOutDate(newCheckOut);
		bookings.setNoOfRooms(totalrooms);

		int noOfDaysStayingInHotel = daysInHotel(newCheckin, newCheckOut);
		
		double totalprice = (double) ((selectedAcRooms.size() * 1500 + selectedNonAcRooms.size() * 1200
				+ selectedDeluxRooms.size() * 2000) * noOfDaysStayingInHotel);
		bookings.setPriceAmount(totalprice);

		bookings.setCustId(HeadersDto.custId);

		bookings.setStaffId(HeadersDto.staffId);

		
		try {

			ReservationDetails book = reservationDetailsRepo.save(bookings);

			if (book != null) {

				int bookingid = book.getBookingId();

				List<RoomDetails> room = new ArrayList<RoomDetails>();
				for (Object i : combinedlist) {
					RoomDetails obj1 = new RoomDetails();
					obj1.setBookingId(bookingid);
					obj1.setRoomId((Integer) i);
//					obj1.setStatus("Occupied");

					room.add(obj1);
				}
				bookings.setRoomdetails(room);

				reservationDetailsRepo.save(bookings);

				return bookingid;
			} else {
				log.error("Method Name:saveReservationRooms ,Error while saving reservation: Booking object is null ");
				throw new ValidationException("Error while saving reservation: Booking object is null",
						CustomHttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			log.error("Method Name:saveReservationRooms message: error while saving reservation");
			throw new ValidationException("error while saving reservation", CustomHttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void validateReservations(List<Integer> tempAcrooms, List<Integer> tempNonAcRooms,
			List<Integer> tempDeluxRooms, Set<Integer> acRoomIds, Set<Integer> nonAcroomIds, Set<Integer> deluxRoomIds,
			RoomsReservationRequestDto bookingdetails) {

		log.info(" method name:validateReservations  called from saveReservation method");

		List<Integer> acRooms = hotelRoomsRepo.getRoomIds("AC");
		List<Integer> nonAcRooms = hotelRoomsRepo.getRoomIds("NON_AC");
		List<Integer> deluxRooms = hotelRoomsRepo.getRoomIds("DELUX");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String fromDateStr = sdf.format(bookingdetails.getCheckInDate());
		String toDateStr = sdf.format(bookingdetails.getCheckOutDate());

		if (acRoomIds.size() + nonAcroomIds.size() + deluxRoomIds.size() == acRooms.size() + nonAcRooms.size()
				+ deluxRooms.size()) {

			log.warn(" method name:validateReservations " + "No Rooms Available from " + fromDateStr + " to "
					+ toDateStr);
			throw new ReservationException("No Rooms Available from " + bookingdetails.getCheckInDate() + " to "
					+ bookingdetails.getCheckOutDate());

		}
		if (tempAcrooms.size() == 0 && bookingdetails.getNoOfAcRooms() != 0) {

			log.warn("Method Name: validateReservations, ac rooms are not available");
			throw new ReservationException("ac rooms are not available ");

		}
		if (tempAcrooms.size() < bookingdetails.getNoOfAcRooms()) {

			int availableAcRooms = (acRooms.size() - acRoomIds.size());

			String result = "  Available AC Rooms : " + availableAcRooms;
			log.warn("Method Name:validateReservations,  Available AC Rooms : " + availableAcRooms);
			throw new ReservationException(result);

		}

		if (tempNonAcRooms.size() == 0 && bookingdetails.getNoOfNonAcRooms() != 0) {

			log.warn("Method Name:validateReservations,  non ac rooms are not available");
			throw new ReservationException("non ac rooms are not available ");

		}
		if (tempNonAcRooms.size() < bookingdetails.getNoOfNonAcRooms()) {
			int availableAcRooms = Math.abs(nonAcRooms.size() - nonAcroomIds.size());
			String result = "  Available NON_AC Rooms : " + availableAcRooms;
			log.warn("Method Name:validateReservations {}", result);
			throw new ReservationException(result);

		}

		if (tempDeluxRooms.size() == 0 && bookingdetails.getNoOfDeluxRooms() != 0) {

			log.warn("Method Name:validateReservations, delux  rooms are not available ");
			throw new ReservationException("delux  rooms are not available ");

		}
		if (tempDeluxRooms.size() < bookingdetails.getNoOfDeluxRooms()) {

			int availableAcRooms = Math.abs(deluxRooms.size() - deluxRoomIds.size());
			String result = "  Available DELUX Rooms : " + availableAcRooms;
			log.warn("Method Name:validateReservations {}", result);
			throw new ReservationException(result);

		}

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

	int daysInHotel(Date newCheckin, Date newCheckOut) {

		long diffInMillies = Math.abs(newCheckOut.getTime() - newCheckin.getTime());
		long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);

		int noOfDaysStayingInHotel = (int) diff / 23;
		return noOfDaysStayingInHotel;

	}

}
