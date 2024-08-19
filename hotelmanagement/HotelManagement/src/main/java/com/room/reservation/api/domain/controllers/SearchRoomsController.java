package com.room.reservation.api.domain.controllers;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.room.reservation.api.domain.enums.CustomHttpStatus;
import com.room.reservation.api.domain.exceptions.ValidationException;
import com.room.reservation.api.domain.services.SearchService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/search-rooms")
@Slf4j
public class SearchRoomsController {

	@Autowired
	SearchService searchservice;
	
//	@Autowired
//	JavaMailSender javamail;

//	@Value("${spring.mail.username}")
//	String fromMail;

	

	@GetMapping("/available")
	public ResponseEntity<Object> searchAvailableRooms(

			@Valid @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
			@Valid @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
			@RequestHeader("staffId") int staffId, @RequestHeader("custId") int customerId)
			throws ParseException, SQLException {

		
		log.info(
				"/api/search-rooms/available  method name: searchAvailableRooms"
						+ "recieved request to search rooms from {} to {} , staffId={} and customerId={}",
				fromDate, toDate, staffId, customerId);
		if (vaidateRequest(fromDate, toDate)) {

			Object response = searchservice.getAvailableRooms(fromDate, toDate, staffId, customerId);

			return ResponseEntity.ok(response);
		}
		log.warn("Method Name:searchAvailableRooms, please enter valid {} and {} ", fromDate, toDate);
		throw new ValidationException("please enter valid fromDate and toDate ", CustomHttpStatus.BAD_REQUEST);

	}

	boolean vaidateRequest(Date fromDate, Date toDate) throws ParseException {

		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = dateFormat.format(date);
		Date date1 = dateFormat.parse(dateStr);

		Optional<Date> from_date = Optional.of(fromDate);
		Optional<Date> to_date = Optional.of(toDate);
		log.info("{},{}", date1, fromDate);

		if (fromDate.equals(toDate)) {
			log.warn("Method Name:vaidateRequest , message: please plan to stay at least a day");
			throw new ValidationException("please plan to stay at least a day ", CustomHttpStatus.Ok);

		}

		else if ((!from_date.isEmpty() & !to_date.isEmpty() & fromDate.before(toDate)
				& (fromDate.after(date1) || fromDate.equals(date1))))

			return true;

		else
			return false;

	}

//	public void listen(String message) {
//
//		// Compose the email message
//		SimpleMailMessage mailMessage = new SimpleMailMessage();
//		mailMessage.setFrom("vajjanagaraju78@gmail.com");
//		mailMessage.setTo("vajjanagaraju78@gmail.com");
//
//		mailMessage.setSubject("Reservation Confirmation");
//		mailMessage.setText("Your reservation with booking ID was successful.");
//
//		// Send the email
////		javamail.send(mailMessage);
//		
//	}
}
