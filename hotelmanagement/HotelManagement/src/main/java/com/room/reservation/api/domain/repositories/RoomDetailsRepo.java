package com.room.reservation.api.domain.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.room.reservation.api.domain.entities.RoomDetails;

@Repository
public interface RoomDetailsRepo extends JpaRepository<RoomDetails, Integer> {

	List<RoomDetails> findByBookingIdAndStatus(int BookingId, String status);

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
