package com.room.reservation.api.domain.services;



import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.room.reservation.api.domain.dtos.ApiResponseDto;
import com.room.reservation.api.domain.dtos.SearchRoomsResponseDto;
import com.room.reservation.api.domain.enums.CustomHttpStatus;
import com.room.reservation.api.domain.exceptions.ValidationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SearchService {

	@Value("${base.url}")
	private String baseUrl;
    private final HttpClient httpClient;

    public SearchService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Object getAvailableRooms(Date fromDate, Date toDate, int staffId, int custId) {

        log.info("Method Name: getAvailableRooms, communicating with search service, running..");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedFromDate = dateFormat.format(fromDate);
        String formattedToDate = dateFormat.format(toDate);

        String url = baseUrl+"/search-rooms/available?fromDate=" + formattedFromDate + "&toDate="
                + formattedToDate;

        try {
            String responseBody = httpClient.makeRequest(url, staffId, custId);

            ObjectMapper objectMapper = new ObjectMapper();
            if(responseBody.startsWith("[") & responseBody.endsWith("]"))
            {
            List<SearchRoomsResponseDto> response = Arrays.asList(objectMapper.readValue(responseBody, SearchRoomsResponseDto[].class));
            return response;
            }
            else
            {
            	return new ObjectMapper().readValue(responseBody, ApiResponseDto.class);
            }
           
        } catch (Exception e) {
            log.error("Method Name: getAvailableRooms, message: Failed to get data from search service");
            e.printStackTrace();
            throw   new ValidationException("Failed to get data from search service",CustomHttpStatus.INTERNAL_SERVER_ERROR);
            
        }
    }
}