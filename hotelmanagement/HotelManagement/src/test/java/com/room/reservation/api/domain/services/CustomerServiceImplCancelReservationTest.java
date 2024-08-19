package com.room.reservation.api.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.room.reservation.api.domain.dtos.CancelReservationRequestDto;
import com.room.reservation.api.domain.dtos.CancelReservationResponseDto;
import com.room.reservation.api.domain.entities.RoomDetails;
import com.room.reservation.api.domain.entities.ReservationDetails;
import com.room.reservation.api.domain.enums.CustomHttpStatus;
import com.room.reservation.api.domain.exceptions.ReservationException;
import com.room.reservation.api.domain.exceptions.ValidationException;
import com.room.reservation.api.domain.repositories.HotelRoomsRepo;
import com.room.reservation.api.domain.repositories.RoomDetailsRepo;
import com.room.reservation.api.domain.repositories.ReservationDetailsRepo;
import com.room.reservation.api.domain.servicempl.CustomerServiceImpl;


@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplCancelReservationTest {

    @Mock
    private ReservationDetailsRepo bookings;

    @Mock
    private RoomDetailsRepo roomdetails;

    @Mock
    private HotelRoomsRepo hotelroomsrepo;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CancelReservationRequestDto cancelRequest;

    @BeforeEach
    public void setUp() {
        cancelRequest = new CancelReservationRequestDto();
        cancelRequest.setBookingId(1);
        cancelRequest.setAcRooms(1);
        cancelRequest.setNonAcRooms(1);
        cancelRequest.setDeluxRooms(1);
    }

    @Test
    public void testCancelReservation_Success() throws Exception {
        // Arrange
        ReservationDetails reservation = new ReservationDetails();
        reservation.setBookingId(1);
        reservation.setPriceAmount(4700);
        reservation.setNoOfRooms(3);
       
        RoomDetails reserved = new RoomDetails();
        reserved.setStatus("Available");

        List<RoomDetails> reservedRooms = Arrays.asList(reserved, reserved, reserved);

        when(bookings.findByBookingId(1)).thenReturn(reservation);
        when(roomdetails.getReservedRoomsOfBookingId(1, "AC")).thenReturn(reservedRooms);
        when(roomdetails.getReservedRoomsOfBookingId(1, "NON_AC")).thenReturn(reservedRooms);
        when(roomdetails.getReservedRoomsOfBookingId(1, "DELUX")).thenReturn(reservedRooms);

        // Act
        CancelReservationResponseDto response = customerService.cancelReservation(cancelRequest);

        // Assert
        assertEquals("Rooms Cancelled Successfully", response.getMessage());
        assertEquals(0, reservation.getPriceAmount());
        assertEquals(0, reservation.getNoOfRooms());
        reservedRooms.forEach(room -> assertEquals("Available", room.getStatus()));
        verify(bookings, times(1)).findByBookingId(1);
        verify(roomdetails, times(1)).getReservedRoomsOfBookingId(1, "AC");
        verify(roomdetails, times(1)).getReservedRoomsOfBookingId(1, "NON_AC");
        verify(roomdetails, times(1)).getReservedRoomsOfBookingId(1, "DELUX");
       verify(roomdetails, times(1)).saveAll(reservedRooms);
        verify(bookings, times(1)).save(reservation);
        verifyNoMoreInteractions(bookings, roomdetails);
    }

    @Test
    public void testCancelReservation_InvalidBookingId_ThrowsException() {
        // Arrange
        when(bookings.findByBookingId(1)).thenReturn(null);

        // Act & Assert
        ReservationException exception = assertThrows(ReservationException.class, () -> {
            customerService.cancelReservation(cancelRequest);
        });
        assertEquals("not found reservation of bookingId: 1", exception.getMessage());
        verify(bookings, times(1)).findByBookingId(1);
        verifyNoMoreInteractions(bookings, roomdetails);
    }

    @Test
    public void testCancelReservation_NoRoomsToCancel_ThrowsException() {
        // Arrange
    	ReservationDetails reservation = new ReservationDetails();
        reservation.setBookingId(1);
        reservation.setPriceAmount(4700);
        reservation.setNoOfRooms(3);
    	
    	cancelRequest.setBookingId(1);
        cancelRequest.setAcRooms(0);
        cancelRequest.setNonAcRooms(0);
        cancelRequest.setDeluxRooms(0);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            customerService.cancelReservation(cancelRequest);
        });
        assertEquals("please enter valid information to cancel rooms associated with reservation id:1", exception.getMessage());
        assertEquals(CustomHttpStatus.BAD_REQUEST, exception.getCustomHttpStatus());
        verifyNoInteractions(bookings, roomdetails);
    }

    // Add more test cases as needed...

    @Test
    public void testCancelReservation_RoomsToCancelExceedsReservedRooms_ThrowsException() {
        // Arrange
    	 cancelRequest = new CancelReservationRequestDto();
         cancelRequest.setBookingId(1);
         cancelRequest.setAcRooms(3);
         cancelRequest.setNonAcRooms(1);
         cancelRequest.setDeluxRooms(1);
        List<RoomDetails> reservedRooms = Arrays.asList(new RoomDetails(), new RoomDetails());
        when(bookings.findByBookingId(1)).thenReturn(new ReservationDetails());
        when(roomdetails.getReservedRoomsOfBookingId(1, "AC")).thenReturn(reservedRooms);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            customerService.cancelReservation(cancelRequest);
        });
        assertEquals("Your Reserved Rooms of selected Room type are : 2", exception.getMessage());
        assertEquals(CustomHttpStatus.BAD_REQUEST, exception.getCustomHttpStatus());
        verify(bookings, times(1)).findByBookingId(1);
        verify(roomdetails, times(1)).getReservedRoomsOfBookingId(1, "AC");
        verifyNoMoreInteractions(bookings, roomdetails);
    }

    @Test
    public void testCancelReservation_UpdateRoomStatusCalledForEachReservedRoom() throws Exception {
        // Arrange
        ReservationDetails reservation = new ReservationDetails();
        reservation.setBookingId(1);
        reservation.setNoOfRooms(2);
        cancelRequest = new CancelReservationRequestDto();
        cancelRequest.setBookingId(1);
        cancelRequest.setAcRooms(4);
        cancelRequest.setNonAcRooms(1);
        cancelRequest.setDeluxRooms(1);
        
        RoomDetails reserved = new RoomDetails();
        reserved.setBookingId(1);
        reserved.setRoomId(1);
        reserved.setStatus("Occupied");
       
       
        
        
        
        List<RoomDetails> reservedRooms = Arrays.asList(new RoomDetails(), new RoomDetails(), new RoomDetails(), new RoomDetails());
        
        when(bookings.findByBookingId(1)).thenReturn(reservation);
        when(roomdetails.getReservedRoomsOfBookingId(1, "AC")).thenReturn(reservedRooms);
        when(roomdetails.getReservedRoomsOfBookingId(1, "NON_AC")).thenReturn(reservedRooms);
        when(roomdetails.getReservedRoomsOfBookingId(1, "DELUX")).thenReturn(reservedRooms);

        // Act
        customerService.cancelReservation(cancelRequest);

        // Assert
        reservedRooms.forEach(room -> assertEquals("Available", room.getStatus()));
        
        
    }

    @Test
    public void testCancelReservation_UpdateReservationWithNewReservationDetails() throws Exception {
        // Arrange
        ReservationDetails reservation = new ReservationDetails();
        reservation.setBookingId(1);
        reservation.setPriceAmount(6000);
        reservation.setNoOfRooms(3);
        cancelRequest = new CancelReservationRequestDto();
        cancelRequest.setBookingId(1);
        cancelRequest.setAcRooms(1);
        cancelRequest.setNonAcRooms(0);
        cancelRequest.setDeluxRooms(0);
        List<RoomDetails> reservedRooms = Arrays.asList(new RoomDetails(), new RoomDetails(), new RoomDetails(), new RoomDetails());
        when(bookings.findByBookingId(1)).thenReturn(reservation);
        when(roomdetails.getReservedRoomsOfBookingId(1, "AC")).thenReturn(reservedRooms);

        // Act
        customerService.cancelReservation(cancelRequest);

        // Assert
        assertEquals(4500, reservation.getPriceAmount());
        assertEquals(2, reservation.getNoOfRooms());
        verify(bookings, times(1)).save(reservation);
        
    }

    @Test
    public void testCancelReservation_RoomsCancellationThrowsException_UpdateReservationNotCalled() throws Exception {
        // Arrange
        ReservationDetails reservation = new ReservationDetails();
        reservation.setBookingId(1);
        List<RoomDetails> reservedRooms = Arrays.asList(new RoomDetails(), new RoomDetails());
        when(bookings.findByBookingId(1)).thenReturn(reservation);
        when(roomdetails.getReservedRoomsOfBookingId(1, "AC")).thenReturn(reservedRooms);
       
        assertThrows(ValidationException.class, () -> {
            customerService.cancelReservation(cancelRequest);
        });
        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            customerService.cancelReservation(cancelRequest);
        });
        verify(bookings, never()).save(reservation);
        
    }
}


