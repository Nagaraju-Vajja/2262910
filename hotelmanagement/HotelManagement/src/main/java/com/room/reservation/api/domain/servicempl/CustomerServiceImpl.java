package com.room.reservation.api.domain.servicempl;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.room.reservation.api.domain.dtos.CancelReservationRequestDto;
import com.room.reservation.api.domain.dtos.CancelReservationResponseDto;
import com.room.reservation.api.domain.dtos.ViewReservationResponseDto;
import com.room.reservation.api.domain.entities.ReservationDetails;
import com.room.reservation.api.domain.entities.RoomDetails;
import com.room.reservation.api.domain.enums.CustomHttpStatus;
import com.room.reservation.api.domain.exceptions.ReservationException;
import com.room.reservation.api.domain.exceptions.ValidationException;
import com.room.reservation.api.domain.repositories.HotelRoomsRepo;
import com.room.reservation.api.domain.repositories.ReservationDetailsRepo;
import com.room.reservation.api.domain.repositories.RoomDetailsRepo;
import com.room.reservation.api.domain.services.CustomerService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

	
	@Autowired
	ReservationDetailsRepo bookings;

	@Autowired
	RoomDetailsRepo roomdetails;

	@Autowired
	HotelRoomsRepo hotelroomsrepo; 

	@Override
	public ViewReservationResponseDto viewReservationDetails(int bookingId) throws SQLException, ParseException {

		log.info("method name : viewReservationDetails booking Id : {} ", bookingId);
		ReservationDetails reservation = bookings.findByBookingId(bookingId);
		if (reservation == null) {
			log.error("not found reservation of booking Id : {}", bookingId);
			throw new ReservationException("not found reservation of bookingId: " + bookingId);
		}
		
		
		int noOfAcRooms = roomdetails.getReservedRoomsOfBookingId(bookingId, "AC").size();
		int noOfNonAcRooms = roomdetails.getReservedRoomsOfBookingId(bookingId, "NON_AC").size();
		int noOfdeluxAcRooms = roomdetails.getReservedRoomsOfBookingId(bookingId, "DELUX").size();
		
		ViewReservationResponseDto response = new ViewReservationResponseDto();
		
		SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
		String date1 = String.valueOf(reservation.getCheckInDate());
		String date2 = String.valueOf(reservation.getCheckOutDate());
		
		Date checkin= dateFormat.parse(date1);
	
		Date checkout= dateFormat.parse(date2);
		
		String checkIndate=dateFormat.format(checkin);
		String checkOutDate = dateFormat.format(checkout);

	
		
		
		response.setCheckInDate((checkIndate));
		response.setCheckOutDate((checkOutDate));
		response.setNoOfRooms(reservation.getNoOfRooms());
		response.setAcRooms(noOfAcRooms);
		response.setNonAcRooms(noOfNonAcRooms);
		response.setDeluxRooms(noOfdeluxAcRooms);

		response.setPricePaid(reservation.getPriceAmount());

		return response;

	}

	@Override
	public CancelReservationResponseDto cancelReservation(CancelReservationRequestDto cancelrequest)
			throws SQLException {
		
		
		

		log.info("  method name: cancelReservation  booking Id: {} ",
				cancelrequest.getBookingId());
		validateCancelRequest(cancelrequest);
		int bookingId = cancelrequest.getBookingId();
		ReservationDetails reservation = bookings.findByBookingId(bookingId);

		if (reservation == null) {
			log.error("not found reservation of booking Id : {}", bookingId);
			throw new ReservationException("not found reservation of bookingId: " + bookingId);
		}
		

		List<RoomDetails> reservedRoomsOfBookingId = getReservedRoomsOfBookingId(cancelrequest, bookingId);
		
		updateRoomStatus(reservedRoomsOfBookingId);

		updateReservationWithNewReservationDetails(cancelrequest, reservation);

		CancelReservationResponseDto response = new CancelReservationResponseDto();
		log.info("Rooms Cancelled Successfully");
		response.setMessage("Rooms Cancelled Successfully");

		return response;
	}

	public void validateCancelRequest(CancelReservationRequestDto cancelrequest) {
		log.info("Method Name:validateCancelRequest, requested from cancelReservation");
		int acRoomsInput = cancelrequest.getAcRooms();
		int nonAcRoomsInput = cancelrequest.getNonAcRooms();
		int deluxRoomsInput = cancelrequest.getDeluxRooms();

		int sumOfRooms = acRoomsInput + nonAcRoomsInput + deluxRoomsInput;
		if (sumOfRooms == 0) {

			int bookingId = cancelrequest.getBookingId();
			log.error("Method Name:validateCancelRequest,  please enter valid information to cancel rooms associated with booking id id:"
					+ String.valueOf(bookingId));
			throw new ValidationException(
					"please enter valid information to cancel rooms associated with reservation id:"
							+ String.valueOf(bookingId),
					CustomHttpStatus.BAD_REQUEST);
		}
	}

	private List<RoomDetails> getReservedRoomsOfBookingId(CancelReservationRequestDto cancelrequest,
			int bookingId) {

		log.info(
				"method name: getReservedRoomsOfBookingId,  requested from cancelReservation method with bookingId: {}",
				cancelrequest.getBookingId());

		
		List<RoomDetails> allReservedRoomsOfBookingId = new ArrayList<>();
		if (cancelrequest.getAcRooms() != 0) {
			List<RoomDetails> reservedAcRooms = roomdetails.getReservedRoomsOfBookingId(bookingId, "AC");
			log.info("{}",reservedAcRooms.size());
			collectAllReservedRoomsOfBookingId(reservedAcRooms, cancelrequest.getAcRooms(),
					allReservedRoomsOfBookingId);
		}
		if (cancelrequest.getNonAcRooms() != 0) {
			List<RoomDetails> reservedNonAcRooms = roomdetails.getReservedRoomsOfBookingId(bookingId, "NON_AC");
			collectAllReservedRoomsOfBookingId(reservedNonAcRooms, cancelrequest.getNonAcRooms(),
					allReservedRoomsOfBookingId);
		}
		if (cancelrequest.getDeluxRooms() != 0) {
			List<RoomDetails> reservedDeluxRooms = roomdetails.getReservedRoomsOfBookingId(bookingId, "DELUX");
			collectAllReservedRoomsOfBookingId(reservedDeluxRooms, cancelrequest.getDeluxRooms(),
					allReservedRoomsOfBookingId);

		}

		return allReservedRoomsOfBookingId;
	}

	private void collectAllReservedRoomsOfBookingId(List<RoomDetails> reservedRooms, int roomsToCancel,
			List<RoomDetails> allReservedRoomsOfBookingId) {
		log.info("Method Name:collectAllReservedRoomsOfBookingId, requested from method getReservedRoomsOfBookingId ");
		
		if (reservedRooms.size() >= roomsToCancel && roomsToCancel != 0) {
			allReservedRoomsOfBookingId.addAll(reservedRooms.subList(0, roomsToCancel));
		} else {
			log.error(" Method Name:collectAllReservedRoomsOfBookingId, Your Reserved Rooms of selected Room type are : " + String.valueOf(reservedRooms.size()));
			throw new ValidationException(
					"Your Reserved Rooms of selected Room type are : " + String.valueOf(reservedRooms.size()),
					CustomHttpStatus.BAD_REQUEST);

		}

	}

	private void updateRoomStatus(List<RoomDetails> reservedRoomsOfBookingId) {

		log.info("method name: updateRoomStatus,  requested from method cancelReservation");
		for (RoomDetails rd : reservedRoomsOfBookingId) {
			rd.setStatus("Available");
			log.info("{},{}",rd.getStatus(),reservedRoomsOfBookingId.size());
		}
		roomdetails.saveAll(reservedRoomsOfBookingId);
	}

	private void updateReservationWithNewReservationDetails(CancelReservationRequestDto cancelrequest,
			ReservationDetails reservation) {
		log.info("method name : updateReservationWithNewReservationDetails requested from cancelReservation");
		int reducedPrice = 1500 * cancelrequest.getAcRooms() + 1200 * cancelrequest.getNonAcRooms()
				+ 2000 * cancelrequest.getDeluxRooms();
		int totalRoomsCancelled = cancelrequest.getAcRooms() + cancelrequest.getNonAcRooms()
				+ cancelrequest.getDeluxRooms();

		double oldPrice = reservation.getPriceAmount();
		int oldNoOfRooms = reservation.getNoOfRooms();
		Date checkin = reservation.getCheckInDate();
		Date checkout = reservation.getCheckOutDate();
		ReservationServiceImpl ob1 = new ReservationServiceImpl();
		int noOfDaysInHotel=ob1.daysInHotel(checkin, checkout);

		reservation.setPriceAmount((oldPrice - reducedPrice*noOfDaysInHotel));
		reservation.setNoOfRooms(oldNoOfRooms - totalRoomsCancelled);

		bookings.save(reservation);
	}

}
