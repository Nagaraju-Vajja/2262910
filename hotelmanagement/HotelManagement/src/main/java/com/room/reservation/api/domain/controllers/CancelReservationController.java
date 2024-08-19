package com.room.reservation.api.domain.controllers;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.room.reservation.api.domain.dtos.CancelReservationRequestDto;
import com.room.reservation.api.domain.dtos.CancelReservationResponseDto;
import com.room.reservation.api.domain.exceptions.ResourceNotFoundException;
import com.room.reservation.api.domain.services.CustomerService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cancel-reservation")
@Slf4j
public class CancelReservationController {

	
	@Autowired
	CustomerService customerservice;

	@PutMapping("/cancel-rooms")
	public ResponseEntity<CancelReservationResponseDto> cancelReservation(
			@Valid @RequestBody CancelReservationRequestDto cancelreservationrequest, @RequestHeader("staffId" ) int staffId, @RequestHeader("custId") int customerId) throws SQLException {
		log.info(" Method Name:cancelReservation , Booking Id:{}  staffId:{} and custometId:{}", cancelreservationrequest.getBookingId(), staffId, customerId);

		CancelReservationResponseDto response = customerservice.cancelReservation(cancelreservationrequest);

		return ResponseEntity.ok(response);
		}


	
	}


