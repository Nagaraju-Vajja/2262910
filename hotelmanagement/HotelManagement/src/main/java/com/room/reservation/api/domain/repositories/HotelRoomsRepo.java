package com.room.reservation.api.domain.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.room.reservation.api.domain.entities.HotelRooms;

@Repository
public interface  HotelRoomsRepo extends JpaRepository<HotelRooms, Integer> {

	@Query("SELECT count(h) from HotelRooms h WHERE h.roomType = :roomType")
	public int countRooms(@Param("roomType") String roomType);
	
	
	
	@Query("SELECT m.roomId from HotelRooms m WHERE m.roomType=:roomType")
	List<Integer> getRoomIds(@Param("roomType") String roomType);
	
	
}
