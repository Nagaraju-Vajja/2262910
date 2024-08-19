package com.room.reservation.api.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.room.reservation.api.domain.entities.ReservationDetails;

@Repository
public interface ReservationDetailsRepo extends JpaRepository<ReservationDetails, Integer>{

	public ReservationDetails  findByBookingId(int bookingId);
	
	

}
