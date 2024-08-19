package com.hotelmanagement.domin.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotelmanagement.domin.entities.RoomDetails;

@Repository
public interface RoomDetailsRepo extends JpaRepository<RoomDetails, Integer> {

	List<RoomDetails> findByBookingIdAndStatus(int BookingId, String status);
	
}