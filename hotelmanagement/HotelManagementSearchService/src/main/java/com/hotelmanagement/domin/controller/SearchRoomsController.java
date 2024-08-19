package com.hotelmanagement.domin.controller;



import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotelmanagement.domin.dtos.SearchRoomsResponseDto;
import com.hotelmanagement.domin.enums.CustomHttpStatus;
import com.hotelmanagement.domin.exceptions.ValidationException;
import com.hotelmanagement.domin.service.SearchRoomsService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/search-rooms")
@Slf4j
public class SearchRoomsController {

	@Autowired
	
	SearchRoomsService roomservice;

	@GetMapping("/available")
	public ResponseEntity<List<SearchRoomsResponseDto>> searchAvailableRooms(

			@Valid @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") String fromDate,
			@Valid @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") String toDate, @RequestHeader("staffId" ) String  staffId, @RequestHeader("custId") String customerId) throws ParseException, SQLException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	
    	//dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date from_date = dateFormat.parse(fromDate);
        Date to_date = dateFormat.parse(toDate);
			log.info("/search-rooms/available  method name: searchAvailableRooms"
					+ "recieved request to search rooms from {} to {}, staffId={} and customerId={}", fromDate, toDate, staffId , customerId );
			if (vaidateRequest(from_date, to_date)) {

				List<SearchRoomsResponseDto> reponseEntity = roomservice.getAvailableRooms(from_date, to_date);

				return ResponseEntity.ok(reponseEntity);
			}
			log.warn("Method Name:searchAvailableRooms, message:please enter valid fromDate and toDate");
			throw new ValidationException("please enter valid fromDate and toDate ", CustomHttpStatus.BAD_REQUEST);
		
			

			
	}

boolean vaidateRequest(Date fromDate, Date toDate) throws ParseException {
		
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr=dateFormat.format(date);
		Date date1= dateFormat.parse(dateStr);
	
        Optional<Date> from_date = Optional.of(fromDate);
        Optional<Date> to_date = Optional.of(toDate);
        log.info("{},{}",date1,fromDate);
		
		if(fromDate.equals(toDate))
		{
			log.warn("Method Name:vaidateRequest , message: please plan to stay at least a day");
			throw new ValidationException("please plan to stay at least a day ", CustomHttpStatus.Ok);
 
		}
 
		else if ((!from_date.isEmpty() & !to_date.isEmpty() & fromDate.before(toDate) & (fromDate.after(date1) || fromDate.equals(date1))) )
			
			return true; 
			
		else
			return false;
 
	}
}
