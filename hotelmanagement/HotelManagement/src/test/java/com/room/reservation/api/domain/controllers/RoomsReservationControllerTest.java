package com.room.reservation.api.domain.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.room.reservation.api.domain.dtos.RoomsReservartionResponseDto;
import com.room.reservation.api.domain.dtos.RoomsReservationRequestDto;
import com.room.reservation.api.domain.enums.CustomHttpStatus;
import com.room.reservation.api.domain.exceptions.ValidationException;
import com.room.reservation.api.domain.services.ReservationService;

@ExtendWith(MockitoExtension.class)
public class RoomsReservationControllerTest {

    @Mock
    private ReservationService service;

    @InjectMocks
    private RoomsReservationController controller;

    @Test
    public void saveReservation_ValidRequest_Success() throws SQLException, ParseException {
        // Arrange
    	RoomsReservationRequestDto requestDto = new RoomsReservationRequestDto();
        String fromdate = "2025-03-23";
        String todate = "2026-03-23";
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        
        
        Date newFromDate = dateFormat.parse(fromdate);
        Date newToDate = dateFormat.parse(todate);
        requestDto.setCheckInDate(newFromDate);
        requestDto.setCheckOutDate(newToDate);
        when(service.reserveHotelRooms(requestDto)).thenReturn(new RoomsReservartionResponseDto());

        // Act
        ResponseEntity<RoomsReservartionResponseDto> responseEntity = controller.reserveHotelRooms(requestDto, 1, 2);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(service, times(1)).reserveHotelRooms(requestDto);
        verifyNoMoreInteractions(service);
    }

    @Test
    public void saveReservation_InvalidDate_ThrowsValidationException() throws SQLException, ParseException {
        // Arrange
        RoomsReservationRequestDto requestDto = new RoomsReservationRequestDto();
        String fromdate = "2027-03-23";
        String todate = "2026-03-23";
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        
        Date newFromDate = dateFormat.parse(fromdate);
        Date newToDate = dateFormat.parse(todate);
        requestDto.setCheckInDate(newFromDate);
        requestDto.setCheckOutDate(newToDate);

       
        ValidationException exception = org.junit.jupiter.api.Assertions.assertThrows(
            ValidationException.class,
            () -> controller.reserveHotelRooms(requestDto, 1, 2)
        );
        assertEquals("checkIndate should be before checkOutdate", exception.getMessage());
      
        verifyNoMoreInteractions(service);
    }

   
}