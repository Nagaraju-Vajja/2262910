package com.room.reservation.api.domain.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.room.reservation.api.domain.dtos.ViewReservationResponseDto;
import com.room.reservation.api.domain.entities.RoomDetails;
import com.room.reservation.api.domain.entities.ReservationDetails;
import com.room.reservation.api.domain.exceptions.ReservationException;
import com.room.reservation.api.domain.repositories.HotelRoomsRepo;
import com.room.reservation.api.domain.repositories.RoomDetailsRepo;
import com.room.reservation.api.domain.repositories.ReservationDetailsRepo;
import com.room.reservation.api.domain.servicempl.CustomerServiceImpl;

public class CustomerServiceImplViewReservationTest {

    @Mock
    private ReservationDetailsRepo bookings;

    @Mock
    private RoomDetailsRepo roomdetails;

    @Mock
    private HotelRoomsRepo hotelroomsrepo;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

//    @Test
//    public void testViewReservationDetails_ValidBookingId_Success() throws SQLException, ParseException {
//        
//        int bookingId = 1;
//        Reservations reservation = new Reservations();
//        reservation.setBookingId(bookingId);
//        Date d1 = new Date();
//        Date d2 = new Date();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String date1 = String.valueOf(d1);
//		String date2 = String.valueOf(d2);
//		
//		Date checkin= dateFormat.parse(date1);
//		Date checkout= dateFormat.parse(date2);
//		 String date3 = String.valueOf(checkin);
//			String date4 = String.valueOf(checkout);
//        
//
//        reservation.setCheckinDate(new Date());
//        reservation.setCheckoutDate(new Date());
//        reservation.setNoOfRooms(2);
//        reservation.setTotalPrice(200.0);
//
//        String checkin1= dateFormat.format(reservation.getCheckinDate());
//        String checkout2= dateFormat.format(reservation.getCheckoutDate());
//
//        when(bookings.findByBookingId(bookingId)).thenReturn(reservation);
//        when(roomdetails.getReservedRoomsOfBookingId(bookingId, "AC")).thenReturn(Arrays.asList(new ReservationDetails()));
//        when(roomdetails.getReservedRoomsOfBookingId(bookingId, "NON_AC")).thenReturn(Arrays.asList(new ReservationDetails()));
//        when(roomdetails.getReservedRoomsOfBookingId(bookingId, "DELUX")).thenReturn(Arrays.asList(new ReservationDetails()));
//
//        // Act
//        ViewReservationResponseDto response = customerService.viewReservationDetails(bookingId);
//
//        
//        assertEquals(checkin1, response.getCheckInDate());
//        assertEquals(checkout2, response.getCheckOutDate());
//        assertEquals(2, response.getNoOfRooms());
//        assertEquals(2, response.getAcRooms());
//        assertEquals(2, response.getNonAcRooms());
//        assertEquals(2, response.getDeluxRooms());
//        assertEquals(200.0, response.getPricePaid());
//    }

    @Test
    public void testViewReservationDetails_InvalidBookingId_ThrowException() {
        // Arrange
        int bookingId = 1;
        when(bookings.findByBookingId(bookingId)).thenReturn(null);

        // Act & Assert
        assertThrows(ReservationException.class, () -> customerService.viewReservationDetails(bookingId));
    }

//    @Test
//    public void testViewReservationDetails_NoRoomsReserved_Success() throws SQLException, ParseException {
//        // Arrange
//        int bookingId = 1;
//        Reservations reservation = new Reservations();
//        reservation.setBookingId(bookingId);
//        reservation.setCheckinDate(new Date());
//        reservation.setCheckoutDate(new Date());
//        reservation.setNoOfRooms(0);
//        reservation.setTotalPrice(0.0);
//
//    	SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
//		String date1 = String.valueOf(reservation.getCheckinDate());
//		String date2 = String.valueOf(reservation.getCheckoutDate());
//		Date checkin= dateFormat.parse(date1);
//		Date checkout= dateFormat.parse(date2);
//		String checkIndate=dateFormat.format(checkin);
//		String checkOutDate = dateFormat.format(checkout);
//
//        when(bookings.findByBookingId(bookingId)).thenReturn(reservation);
//        when(roomdetails.getReservedRoomsOfBookingId(bookingId, "AC")).thenReturn(Arrays.asList());
//        when(roomdetails.getReservedRoomsOfBookingId(bookingId, "NON_AC")).thenReturn(Arrays.asList());
//        when(roomdetails.getReservedRoomsOfBookingId(bookingId, "DELUX")).thenReturn(Arrays.asList());
//
//        // Act
//        ViewReservationResponseDto response = customerService.viewReservationDetails(bookingId);
//
//        // Assert
//        assertEquals(checkIndate, response.getCheckInDate());
//        assertEquals(checkOutDate, response.getCheckOutDate());
//        assertEquals(0, response.getNoOfRooms());
//        assertEquals(0, response.getAcRooms());
//        assertEquals(0, response.getNonAcRooms());
//        assertEquals(0, response.getDeluxRooms());
//        assertEquals(0.0, response.getPricePaid());
//    }

}
