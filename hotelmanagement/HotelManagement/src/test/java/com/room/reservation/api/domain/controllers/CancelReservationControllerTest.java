package com.room.reservation.api.domain.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.room.reservation.api.domain.dtos.CancelReservationRequestDto;
import com.room.reservation.api.domain.dtos.CancelReservationResponseDto;
import com.room.reservation.api.domain.exceptions.ReservationException;
import com.room.reservation.api.domain.exceptions.ResourceNotFoundException;
import com.room.reservation.api.domain.exceptions.ValidationException;
import com.room.reservation.api.domain.services.CustomerService;

@ExtendWith(MockitoExtension.class)
public class CancelReservationControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CancelReservationController cancelReservationController;

    @Test
    public void testCancelReservationSuccess() throws Exception {
        // Arrange
        CancelReservationRequestDto requestDto = new CancelReservationRequestDto();
        requestDto.setBookingId(123);

        CancelReservationResponseDto responseDto = new CancelReservationResponseDto();
        responseDto.setMessage("Reservation cancelled successfully");

        when(customerService.cancelReservation(requestDto)).thenReturn(responseDto);

        // Act
        ResponseEntity<CancelReservationResponseDto> response = cancelReservationController.cancelReservation(requestDto, 1, 1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
    }

   

    @Test
    public void testCancelReservationNoInteractionWithService() throws Exception {
        // Arrange
        CancelReservationRequestDto requestDto = new CancelReservationRequestDto();
        requestDto.setBookingId(123);

        // Act
        cancelReservationController.cancelReservation(requestDto, 1, 1);

        // Assert
        verify(customerService, times(1)).cancelReservation(requestDto);
        verifyNoMoreInteractions(customerService);
    }
}