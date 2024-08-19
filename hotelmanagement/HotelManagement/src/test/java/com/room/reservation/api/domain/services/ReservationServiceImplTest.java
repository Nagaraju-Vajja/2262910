package com.room.reservation.api.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.room.reservation.api.domain.dtos.HeadersDto;
import com.room.reservation.api.domain.dtos.RoomsReservartionResponseDto;
import com.room.reservation.api.domain.dtos.RoomsReservationRequestDto;
import com.room.reservation.api.domain.entities.ReservationDetails;
import com.room.reservation.api.domain.enums.CustomHttpStatus;
import com.room.reservation.api.domain.exceptions.ReservationException;
import com.room.reservation.api.domain.exceptions.ValidationException;
import com.room.reservation.api.domain.repositories.HotelRoomsRepo;
import com.room.reservation.api.domain.repositories.RoomDetailsRepo;
import com.room.reservation.api.domain.repositories.ReservationDetailsRepo;
import com.room.reservation.api.domain.servicempl.ReservationServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplTest {

    @Mock
    private ReservationDetailsRepo bookingrepo;

    @Mock
    private HotelRoomsRepo hotelroomsrepo; 
    
    @Mock
    private HeadersDto headersdto;

    @Mock
    private RoomDetailsRepo roomdetailsrepo;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Test
    public void testSaveReservation_NoRoomsAvailable() {
        RoomsReservationRequestDto requestDto = new RoomsReservationRequestDto();
        requestDto.setNoOfAcRooms(1);
        requestDto.setNoOfNonAcRooms(1);
        requestDto.setNoOfDeluxRooms(1);
        requestDto.setCheckInDate(new Date());
        requestDto.setCheckOutDate(new Date());

        
        when(roomdetailsrepo.getAllReservedRoomsInRangeOfFromDateToDate(requestDto.getCheckInDate(), requestDto.getCheckOutDate())).thenReturn(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21));
        when(hotelroomsrepo.getRoomIds("AC")).thenReturn(Arrays.asList(1,2,3,4,5,6,7));
        when(hotelroomsrepo.getRoomIds("NON_AC")).thenReturn(Arrays.asList(8,9,10,11,12,13));
        when(hotelroomsrepo.getRoomIds("DELUX")).thenReturn(Arrays.asList(14,15,16,17,18,19,20,21));
        ReservationException exception = org.junit.jupiter.api.Assertions.assertThrows(ReservationException.class,
                () -> reservationService.reserveHotelRooms(requestDto));

        assertEquals("No Rooms Available from " + requestDto.getCheckInDate() + " to " + requestDto.getCheckOutDate(),
                exception.getMessage()); 
    }

    @Test
    public void testSaveReservation_ValidationException() {
        RoomsReservationRequestDto requestDto = new RoomsReservationRequestDto();
        requestDto.setNoOfAcRooms(0);
        requestDto.setNoOfNonAcRooms(0);
        requestDto.setNoOfDeluxRooms(0);

        ValidationException exception = org.junit.jupiter.api.Assertions.assertThrows(ValidationException.class,
                () -> reservationService.reserveHotelRooms(requestDto));

        assertEquals("please enter required details for reservation", exception.getMessage());
        assertEquals(CustomHttpStatus.BAD_REQUEST, exception.getCustomHttpStatus());
    }
//    @Test
//    public void testSaveReservation_Success() {
//    	 RoomsReservationRequestDto requestDto = new RoomsReservationRequestDto();
//         requestDto.setNoOfAcRooms(1);
//         requestDto.setNoOfNonAcRooms(1);
//         requestDto.setNoOfDeluxRooms(1);
//         requestDto.setCheckInDate(new Date());
//         requestDto.setCheckOutDate(new Date());
//
//        List<Integer> acRooms = new ArrayList<>();
//        acRooms.add(1);
//        acRooms.add(2);
//        
//        when(hotelroomsrepo.getRoomIds("AC")).thenReturn(Arrays.asList(1,2,3,4,5,6,7));
//        when(hotelroomsrepo.getRoomIds("NON_AC")).thenReturn(Arrays.asList(8,9,10,11,12,13));
//        when(hotelroomsrepo.getRoomIds("DELUX")).thenReturn(Arrays.asList(14,15,16,17,18,19,20,21));
//        when(roomdetailsrepo.getAllReservedRoomsInRangeOfFromDateToDate(requestDto.getCheckInDate(), requestDto.getCheckOutDate())).thenReturn(Arrays.asList(1,2,3,8,9,14,15));
//        
//        ReservationDetails bookings = new ReservationDetails();
//        bookings.setBookingId(1);
//        bookings.setCustId(1);
//        bookings.setStaffId(1);
//       
//        
//        when(bookingrepo.save(any(ReservationDetails.class))).thenReturn(bookings);
//
//        RoomsReservartionResponseDto responseDto = reservationService.reserveHotelRooms(requestDto);
//
//        assertEquals("rooms reserved successfully with booking Id: 1", responseDto.getMessage());
//    }
//   

    @Test
    public void testSelectRooms_Success() {
        RoomsReservationRequestDto requestDto = new RoomsReservationRequestDto();
        requestDto.setNoOfAcRooms(1);
        
        requestDto.setCheckInDate(new Date());
        requestDto.setCheckOutDate(new Date());

        List<Integer> acRooms = new ArrayList<>();
        acRooms.add(1);
        acRooms.add(2);
        ReservationDetails bookings = new ReservationDetails();
       
        when(bookingrepo.save(any(ReservationDetails.class))).thenReturn(bookings);
       

        String result = reservationService.selectRooms(acRooms, new ArrayList<>(), new ArrayList<>(), requestDto);

        assertEquals("rooms reserved successfully with booking Id: 0", result);
    }

    @Test
    public void testSaveReservationRooms_Success() {
        RoomsReservationRequestDto requestDto = new RoomsReservationRequestDto();
        requestDto.setNoOfAcRooms(1);
        requestDto.setCheckInDate(new Date());
        requestDto.setCheckOutDate(new Date());

        ReservationDetails reservation = new ReservationDetails();
        reservation.setBookingId(1);

        when(bookingrepo.save(any(ReservationDetails.class))).thenReturn(reservation);
        
        

        int bookingId = reservationService.saveReservationRooms(requestDto, new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>());

        assertEquals(1, bookingId);
    }

    
 
    @Test
    public void testValidateReservations_ACRoomsNotAvailable() {
    	 RoomsReservationRequestDto requestDto = new RoomsReservationRequestDto();
         requestDto.setNoOfAcRooms(1);
        
         requestDto.setCheckInDate(new Date());
         requestDto.setCheckOutDate(new Date());

         List<Integer> nonAcRooms= Arrays.asList(8,9,10,11);
         List<Integer> deluxRooms= Arrays.asList(14,15,16,17);
       
        when(hotelroomsrepo.getRoomIds("AC")).thenReturn(Arrays.asList(1,2,3,4,5,6,7));
        when(hotelroomsrepo.getRoomIds("NON_AC")).thenReturn(Arrays.asList(8,9,10,11,12,13));
        when(hotelroomsrepo.getRoomIds("DELUX")).thenReturn(Arrays.asList(14,15,16,17,18,19,20,21));

        Set<Integer> acRoomIds = new HashSet<>();
        acRoomIds.add(1);
        acRoomIds.add(2);
        acRoomIds.add(3);
        acRoomIds.add(4);

        ReservationException exception = org.junit.jupiter.api.Assertions.assertThrows(ReservationException.class,
                () -> reservationService.validateReservations(new ArrayList<Integer>(), nonAcRooms,deluxRooms,
                		acRoomIds, new HashSet<Integer>(), new HashSet<Integer>(), requestDto));

        assertEquals("ac rooms are not available ", exception.getMessage());
    }

    @Test
    public void testValidateReservations_NonACRoomsNotAvailable() {
    	RoomsReservationRequestDto requestDto = new RoomsReservationRequestDto();
        requestDto.setNoOfNonAcRooms(1);
        
        requestDto.setCheckInDate(new Date());
        requestDto.setCheckOutDate(new Date());
        
        List<Integer> acRooms= Arrays.asList(1,2,3,4);
        List<Integer> deluxRooms= Arrays.asList(14,15,16,17);

       
        when(hotelroomsrepo.getRoomIds("AC")).thenReturn(Arrays.asList(1,2,3,4,5,6,7));
        when(hotelroomsrepo.getRoomIds("NON_AC")).thenReturn(Arrays.asList(8,9,10,11,12,13));
        when(hotelroomsrepo.getRoomIds("DELUX")).thenReturn(Arrays.asList(14,15,16,17,18,19,20,21));


        Set<Integer> nonAcroomIds = new HashSet<>();
        nonAcroomIds.add(8);
        nonAcroomIds.add(9);
        nonAcroomIds.add(10);
        nonAcroomIds.add(11);

        ReservationException exception = org.junit.jupiter.api.Assertions.assertThrows(ReservationException.class,
                () -> reservationService.validateReservations(acRooms, new ArrayList<>(), deluxRooms,
                        new HashSet<>(),
                nonAcroomIds, new HashSet<>(), requestDto));

        assertEquals("non ac rooms are not available ", exception.getMessage());
    }

    @Test
    public void testValidateReservations_DeluxRoomsNotAvailable() {
    	RoomsReservationRequestDto requestDto = new RoomsReservationRequestDto();
        requestDto.setNoOfDeluxRooms(1);
       
        requestDto.setCheckInDate(new Date());
        requestDto.setCheckOutDate(new Date());

        List<Integer> deluxRooms = new ArrayList<>();
        deluxRooms.add(1);
        deluxRooms.add(2);
        deluxRooms.add(3);
        deluxRooms.add(4);
        when(hotelroomsrepo.getRoomIds("AC")).thenReturn(Arrays.asList(1,2,3,4,5,6,7));
        when(hotelroomsrepo.getRoomIds("NON_AC")).thenReturn(Arrays.asList(8,9,10,11,12,13));
        when(hotelroomsrepo.getRoomIds("DELUX")).thenReturn(Arrays.asList(14,15,16,17,18,19,20,21));

        Set<Integer> deluxRoomIds = new HashSet<>();
        deluxRoomIds.add(1);
        deluxRoomIds.add(2);
        deluxRoomIds.add(3);
        deluxRoomIds.add(4);
        
        List<Integer> acRooms= Arrays.asList(1,2,3,4);
        List<Integer> nonAcRooms= Arrays.asList(8,9,10);
       

        ReservationException exception = org.junit.jupiter.api.Assertions.assertThrows(ReservationException.class,
                () -> reservationService.validateReservations(acRooms,nonAcRooms, new ArrayList<>(),
                        new HashSet<>(), new HashSet<>(), deluxRoomIds, requestDto));

        assertEquals("delux  rooms are not available ", exception.getMessage());
    }

    @Test
    public void testSaveReservationRooms_BookingObjectIsNull() {
    	 RoomsReservationRequestDto requestDto = new RoomsReservationRequestDto();
         requestDto.setNoOfAcRooms(1);
         
         requestDto.setCheckInDate(new Date());
         requestDto.setCheckOutDate(new Date());
        when(bookingrepo.save(any(ReservationDetails.class))).thenReturn(null);

        ValidationException exception = org.junit.jupiter.api.Assertions.assertThrows(ValidationException.class,
                () -> reservationService.saveReservationRooms(requestDto, new ArrayList<>(), new ArrayList<>(),
                        new ArrayList<>(), new ArrayList<>()));

        assertEquals("error while saving reservation", exception.getMessage());
        assertEquals(CustomHttpStatus.INTERNAL_SERVER_ERROR, exception.getCustomHttpStatus());
    }
}