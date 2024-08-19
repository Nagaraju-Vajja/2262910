package com.room.reservation.api.domain.entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class HotelRooms {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int roomId;
	private double pricePerDay;
	private String roomType;

	@OneToMany
	@JoinColumn(name = "RoomId")
	private List<RoomDetails> roomDetails;

	
}
