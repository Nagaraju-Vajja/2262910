package com.room.reservation.api.domain.services;

import java.sql.SQLException;

import com.room.reservation.api.domain.dtos.RoomsReservartionResponseDto;
import com.room.reservation.api.domain.dtos.RoomsReservationRequestDto;

public interface ReservationService {

	RoomsReservartionResponseDto reserveHotelRooms(RoomsReservationRequestDto bookingdetails) throws SQLException;
}
