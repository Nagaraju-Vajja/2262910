package com.room.reservation.api.domain.controllers;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.text.ParseException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.room.reservation.api.domain.dtos.ViewReservationResponseDto;
import com.room.reservation.api.domain.exceptions.ReservationException;
import com.room.reservation.api.domain.exceptions.ValidationException;
import com.room.reservation.api.domain.services.CustomerService;

@ExtendWith(MockitoExtension.class)
class ViewReservationControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private ViewReservationController viewReservationController;

    @Test
    void testViewReservation_Successful() throws ParseException, SQLException {
        // Arrange
        int bookingId = 123;
        int staffId = 456;
        int customerId = 789;
        ViewReservationResponseDto responseDto = new ViewReservationResponseDto();
        when(customerService.viewReservationDetails(bookingId)).thenReturn(responseDto);

        // Act
        ResponseEntity<ViewReservationResponseDto> responseEntity = viewReservationController.viewReservation(bookingId, staffId, customerId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseDto, responseEntity.getBody());
        verify(customerService, times(1)).viewReservationDetails(bookingId);
    }

    @Test
    void testViewReservation_BookingIdNotFound() throws ParseException, SQLException {
        // Arrange
        int bookingId = 1000000;
        
       when(customerService.viewReservationDetails(bookingId)).thenThrow(new ReservationException("not found reservation of bookingId: " + bookingId));

       
        ReservationException exception = org.junit.jupiter.api.Assertions.assertThrows(
        		ReservationException.class,
                () -> customerService.viewReservationDetails(bookingId)
            );
        // Assert
        assertEquals("not found reservation of bookingId: " + bookingId,exception.getMessage());
        verify(customerService, times(1)).viewReservationDetails(bookingId);
    }

   
}