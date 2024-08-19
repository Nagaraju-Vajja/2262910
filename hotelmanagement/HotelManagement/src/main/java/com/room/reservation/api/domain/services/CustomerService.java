package com.room.reservation.api.domain.services;

import java.sql.SQLException;
import java.text.ParseException;

import com.room.reservation.api.domain.dtos.CancelReservationRequestDto;
import com.room.reservation.api.domain.dtos.CancelReservationResponseDto;
import com.room.reservation.api.domain.dtos.ViewReservationResponseDto;

public interface CustomerService {

	ViewReservationResponseDto viewReservationDetails(int bookingId) throws SQLException, ParseException;

	CancelReservationResponseDto cancelReservation(CancelReservationRequestDto cancelrequest) throws SQLException;
}
