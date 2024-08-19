
package com.room.reservation.api.domain.controllers;

import java.sql.SQLException;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.room.reservation.api.domain.dtos.HeadersDto;
import com.room.reservation.api.domain.dtos.RoomsReservartionResponseDto;
import com.room.reservation.api.domain.dtos.RoomsReservationRequestDto;
import com.room.reservation.api.domain.enums.CustomHttpStatus;
import com.room.reservation.api.domain.exceptions.ValidationException;
import com.room.reservation.api.domain.kafka.KafkaProducer;
import com.room.reservation.api.domain.services.ReservationService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/reservations/api")
@Slf4j
public class RoomsReservationController {

	@Autowired
	ReservationService service;
	@Autowired
	KafkaProducer kafkaProducer;

	@PostMapping("/reserve-rooms")
	public ResponseEntity<RoomsReservartionResponseDto> reserveHotelRooms(
			@Valid @RequestBody RoomsReservationRequestDto requestdto, @RequestHeader("staffId") int staffId,
			@RequestHeader("custId") int customerId) throws SQLException, ParseException {

		log.info(
				"/reservations/api/reserve-rooms  method name: saveReservation "
						+ " recived request to save reservation {}  and staffId={} and customerId={}",
				requestdto, staffId, customerId);
		
		
//		kafkaProducer.sendMessage("sending message from RoomsReservationController");
		
		HeadersDto.custId = customerId;
		HeadersDto.staffId = staffId;
		SearchRoomsController searchRooms = new SearchRoomsController();
		if (searchRooms.vaidateRequest(requestdto.getCheckInDate(), requestdto.getCheckOutDate())) {

			RoomsReservartionResponseDto response = service.reserveHotelRooms(requestdto);
			return ResponseEntity.ok(response);
		}
		log.warn(
				"/reservations/api/reserve-rooms Method Name :saveReservation, message:checkIndate should be before checkOutdate");
		throw new ValidationException("checkIndate should be before checkOutdate", CustomHttpStatus.Ok);

	}

}
