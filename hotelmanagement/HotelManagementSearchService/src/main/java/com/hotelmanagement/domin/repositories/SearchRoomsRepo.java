package com.hotelmanagement.domin.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hotelmanagement.domin.entities.HotelRooms;
import com.hotelmanagement.domin.entities.RoomDetails;

@Repository
public interface SearchRoomsRepo extends JpaRepository<HotelRooms, Integer> {
	
	@Query("SELECT count(h) from HotelRooms h WHERE h.roomType = :roomType")
	public int countRooms(@Param("roomType") String roomType);
	
	
	
	@Query("SELECT m.roomId from HotelRooms m WHERE m.roomType=:roomType")
	List<Integer> getRoomIds(@Param("roomType") String roomType);
	
	@Query("SELECT DISTINCT rd.roomId FROM RoomDetails rd JOIN ReservationDetails rs ON rd.bookingId = rs.bookingId"
			+ " WHERE ( rs.checkInDate<= :fromDate AND rs.checkOutDate >:fromDate "
			+ " OR rs.checkInDate >= :fromDate AND rs.checkInDate <=:toDate)" + " AND rd.status='Occupied'")
	List<Integer> getAllReservedRoomsInRangeOfFromDateToDate(@Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate);

	@Query("SELECT rd FROM RoomDetails rd JOIN HotelRooms hr ON rd.roomId= hr.roomId "
			+ "WHERE hr.roomType=:roomType AND rd.bookingId=:bookingId AND rd.status='Occupied'")
	public List<RoomDetails> getReservedRoomsOfBookingId(@Param("bookingId") int bookingId,
			@Param("roomType") String roomType);
	
	
	

}

	

