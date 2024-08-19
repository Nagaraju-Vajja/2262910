package com.room.reservation.api.domain.entities;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class ReservationDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int bookingId;

	private int custId;

	private int staffId;

	private Date checkInDate;
	private Date checkOutDate;
	private int noOfRooms;
	private double priceAmount;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "BookingId")
	private List<RoomDetails> roomdetails;

	
	

}
