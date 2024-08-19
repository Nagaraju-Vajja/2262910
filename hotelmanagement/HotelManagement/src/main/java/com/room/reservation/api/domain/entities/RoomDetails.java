package com.room.reservation.api.domain.entities;

import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class RoomDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int rid;
	private int bookingId;
	private String status;
	private int roomId;
	
	
	
}
