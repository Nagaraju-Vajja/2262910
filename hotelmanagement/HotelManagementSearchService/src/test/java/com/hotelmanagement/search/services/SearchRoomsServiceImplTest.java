package com.hotelmanagement.search.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hotelmanagement.domin.dtos.SearchRoomsResponseDto;
import com.hotelmanagement.domin.exceptions.ReservationException;
import com.hotelmanagement.domin.repositories.SearchRoomsRepo;
import com.hotelmanagement.domin.serviceimpl.SearchRoomsServiceImple;

class SearchRoomsServiceImplTest {

    @Mock
    SearchRoomsRepo hotelroomsrep;

    @InjectMocks
    SearchRoomsServiceImple searchRoomService;

     
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAvailableRooms_NoReservations_ReturnsAllAvailableRooms() throws SQLException {
        // Arrange
        Date fromDate = new Date();
        Date toDate = new Date();
        when(hotelroomsrep.getAllReservedRoomsInRangeOfFromDateToDate(fromDate, toDate)).thenReturn(new ArrayList<>());

        // Act
        List<SearchRoomsResponseDto> result = searchRoomService.getAvailableRooms(fromDate, toDate);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("AC", result.get(0).getRoomType());
        assertEquals("NON_AC", result.get(1).getRoomType());
        assertEquals("DELUX", result.get(2).getRoomType());
    }

    @Test
    void getAvailableRooms_NoAvailableRooms_ThrowsReservationException() throws SQLException {
        // Arrange
        Date fromDate = new Date();
        Date toDate = new Date();
        List<Integer> acRooms= List.of(1,2,3,4,5,6,7);
        List<Integer> nonAcRooms= List.of(8,9,10,11,12,13);
        List<Integer> deluxRooms= List.of(14,15,16,17,18,19,20,21);
        List<Integer> reservedRooms = List.of(1, 2, 3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21);
        when(hotelroomsrep.getRoomIds("AC")).thenReturn(acRooms);
        when(hotelroomsrep.getRoomIds("NON_AC")).thenReturn(nonAcRooms);
        when(hotelroomsrep.getRoomIds("DELUX")).thenReturn(deluxRooms);
        when(hotelroomsrep.countRooms("AC")).thenReturn(7);
        when(hotelroomsrep.countRooms("NON_AC")).thenReturn(6);
        when(hotelroomsrep.countRooms("DELUX")).thenReturn(8);
        when(hotelroomsrep.getAllReservedRoomsInRangeOfFromDateToDate(fromDate, toDate)).thenReturn(reservedRooms);

        // Act & Assert
        assertThrows(ReservationException.class, () -> {
            searchRoomService.getAvailableRooms(fromDate, toDate);
        });
        
    }

    @Test
    void getAvailableRooms_RoomsAvailable_ReturnsAvailableRooms() throws SQLException {
        // Arrange
        Date fromDate = new Date();
        Date toDate = new Date();
        List<Integer> reservedRooms = List.of(1, 2);
        List<Integer> allRooms = List.of(1, 2, 8, 14, 15);
        when(hotelroomsrep.getAllReservedRoomsInRangeOfFromDateToDate(fromDate, toDate)).thenReturn(reservedRooms);
        when(hotelroomsrep.countRooms("AC")).thenReturn(2);
        when(hotelroomsrep.countRooms("NON_AC")).thenReturn(2);
        when(hotelroomsrep.countRooms("DELUX")).thenReturn(1);
        when(hotelroomsrep.getRoomIds("AC")).thenReturn(allRooms);
        when(hotelroomsrep.getRoomIds("NON_AC")).thenReturn(allRooms);
        when(hotelroomsrep.getRoomIds("DELUX")).thenReturn(allRooms);

        // Act
        List<SearchRoomsResponseDto> result = searchRoomService.getAvailableRooms(fromDate, toDate);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("AC", result.get(0).getRoomType());
        assertEquals("NON_AC", result.get(1).getRoomType());
        assertEquals("DELUX", result.get(2).getRoomType());
        assertEquals(3, result.get(0).getTotalAvailableRooms());
        assertEquals(5, result.get(1).getTotalAvailableRooms());
        assertEquals(5, result.get(2).getTotalAvailableRooms());
    }

    @Test
    void availableRoomsInRangeOfFromDateToDate_ReturnsAvailableRoomsDtoList() {
        // Act
        List<SearchRoomsResponseDto> result = searchRoomService.availableRoomsInRangeOfFromDateToDate(2, 2, 1);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("AC", result.get(0).getRoomType());
        assertEquals("NON_AC", result.get(1).getRoomType());
        assertEquals("DELUX", result.get(2).getRoomType());
        assertEquals(2, result.get(0).getTotalAvailableRooms());
        assertEquals(2, result.get(1).getTotalAvailableRooms());
        assertEquals(1, result.get(2).getTotalAvailableRooms());
    }

   @Test
   void getAvailableRooms_NoReservations_ReturnAllAvailableRooms() throws SQLException
   {
	   Date fromDate = new Date();
	   Date toDate = new Date();
       when(hotelroomsrep.getAllReservedRoomsInRangeOfFromDateToDate(fromDate, toDate)).thenReturn(new ArrayList<>());
       when(hotelroomsrep.countRooms("AC")).thenReturn(7);
       when(hotelroomsrep.countRooms("NON_AC")).thenReturn(6);
       when(hotelroomsrep.countRooms("DELUX")).thenReturn(8);
       List<SearchRoomsResponseDto> available=searchRoomService.getAvailableRooms(fromDate, toDate);
       assertEquals("AC", available.get(0).getRoomType());
       assertEquals("NON_AC", available.get(1).getRoomType());
       assertEquals("DELUX", available.get(2).getRoomType());
       assertEquals(7,available.get(0).getTotalAvailableRooms());
       assertEquals(6,available.get(1).getTotalAvailableRooms());
       assertEquals(8,available.get(2).getTotalAvailableRooms());
   }
}
