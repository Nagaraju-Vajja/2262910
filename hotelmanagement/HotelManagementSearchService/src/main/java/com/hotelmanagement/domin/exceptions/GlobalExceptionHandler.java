package com.hotelmanagement.domin.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hotelmanagement.domin.dtos.ApiResponseDto;
import com.hotelmanagement.domin.enums.CustomHttpStatus;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	
	
	

	  
	

	    @ExceptionHandler(ValidationException.class)
	    public ResponseEntity<ApiResponseDto> validationExceptionHandler(ValidationException ex) {
	        String message = ex.getMessage();
	        log.error("Validation exception: {}", message, ex);
	        
	        ApiResponseDto apiResponse = new ApiResponseDto(message, false);
	        return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(ex.getCustomHttpStatus().getCode()));
	    }

//	    @ExceptionHandler(MethodArgumentNotValidException.class)
//	    public ResponseEntity<Map<String, String>> handleMethodArgsNotvalidException(MethodArgumentNotValidException ex) {
//	        Map<String, String> response = new HashMap<>();
//	        ex.getBindingResult().getAllErrors().forEach((error) -> {
//	            String filedName = ((FieldError) error).getField();
//	            String errorMessage = error.getDefaultMessage();
//	            response.put(filedName, errorMessage);
//	        });
//
//	        log.error("Validation failed for method arguments: {}", response, ex);
//
//	        return new ResponseEntity<>(response,HttpStatus.valueOf(CustomHttpStatus.BAD_REQUEST.getCode()));
//	    }

	    @ExceptionHandler(ReservationException.class)
	    public ResponseEntity<ApiResponseDto> handleReservationException(ReservationException ex) {
	        String message = ex.getMessage();
	        log.error("Reservation exception: {}", message, ex);

	        ApiResponseDto response = new ApiResponseDto(message, false);
	        return new ResponseEntity<>(response, HttpStatus.valueOf(CustomHttpStatus.Ok.getCode()));
	    }
	    
	    
	    @ExceptionHandler(MissingServletRequestParameterException.class)
	    public ResponseEntity<Object> handleMissingParams(MissingServletRequestParameterException ex) {
	       
	        Map<String, String> errorMap = new HashMap<>();
	        errorMap.put(ex.getParameterName()," Is Required. ");
	        return ResponseEntity.status(HttpStatus.valueOf(CustomHttpStatus.BAD_REQUEST.getCode())).body(errorMap);
	    }
	}
	
	

