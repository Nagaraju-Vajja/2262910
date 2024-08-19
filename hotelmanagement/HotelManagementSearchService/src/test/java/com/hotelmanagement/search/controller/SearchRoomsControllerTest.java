package com.hotelmanagement.search.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.hotelmanagement.domin.controller.SearchRoomsController;
import com.hotelmanagement.domin.dtos.SearchRoomsResponseDto;
import com.hotelmanagement.domin.enums.CustomHttpStatus;
import com.hotelmanagement.domin.exceptions.ValidationException;
import com.hotelmanagement.domin.service.SearchRoomsService;

class SearchRoomsControllerTest {

    @Mock
    private SearchRoomsService roomService;

    @InjectMocks
    private SearchRoomsController searchRoomsController;

    @BeforeEach
    void setUp() { 
        MockitoAnnotations.openMocks(this);
    } 

    @Test
    void searchAvailableRooms_ValidDates_ReturnsAvailableRooms() throws ParseException, SQLException {
        // Arrange
    	SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd");
    	
       String fromDateStr = dateFormate.format( new Date(System.currentTimeMillis() +86400000));
       String toDateStr = dateFormate.format(  new Date(System.currentTimeMillis() +86400000+86400000));
       Date fromDate= dateFormate.parse(fromDateStr);
       Date toDate= dateFormate.parse(toDateStr);
        
        List<SearchRoomsResponseDto> response = new ArrayList<>();
        when(roomService.getAvailableRooms(fromDate, toDate)).thenReturn(response);

        // Act
        
        ResponseEntity<List<SearchRoomsResponseDto>> result = searchRoomsController.searchAvailableRooms(fromDateStr, toDateStr, "staffId", "customerId");

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        
        assertSame(response, result.getBody());
        verify(roomService, times(1)).getAvailableRooms(fromDate, toDate);
        verifyNoMoreInteractions(roomService);
    }

    @Test
    void searchAvailableRooms_InvalidDates_ThrowsValidationException() throws ParseException, SQLException {
        // Arrange
        Date fromDate = new Date();
        Date toDate = new Date(System.currentTimeMillis() - 86400000); // One day before current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateStr= dateFormat.format(fromDate) ;
        String toDateStr= dateFormat.format(toDate) ;
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            searchRoomsController.searchAvailableRooms(fromDateStr, toDateStr, "staffId", "customerId");
        });

        
        assertEquals("please enter valid fromDate and toDate ", exception.getMessage());
        assertEquals(CustomHttpStatus.BAD_REQUEST, exception.getCustomHttpStatus());
        verifyNoInteractions(roomService);
    }

   
}
