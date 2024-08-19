package com.room.reservation.api.domain.controllers;

import java.sql.SQLException;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.room.reservation.api.domain.dtos.ViewReservationResponseDto;
import com.room.reservation.api.domain.services.CustomerService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/view-api")
@Slf4j
public class ViewReservationController {

	
	@Autowired
	CustomerService customerservice;

	@GetMapping("/view-reservation")
	public ResponseEntity<ViewReservationResponseDto> viewReservation(@Valid @RequestParam("bookingId") int bookingId, @RequestHeader("staffId" ) int staffId, @RequestHeader("custId") int customerId)throws SQLException, ParseException {
		
			log.info("/view-api/view-reservation Method Name: viewReservation,  booking Id: {} staffId:{} and customerId:{}", bookingId, staffId, customerId);
			ViewReservationResponseDto response = customerservice.viewReservationDetails(bookingId);
			return ResponseEntity.ok(response);

		
		

	}

}
