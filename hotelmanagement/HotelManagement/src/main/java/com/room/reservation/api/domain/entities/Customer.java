package com.room.reservation.api.domain.entities;

import java.util.Date;
import java.util.List;

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
public class Customer {

	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int CustId;

	private String firstName;
	private String lastName;
	private Date dateOfBirth;
	private String phoneNo;
	private String email;
	@OneToMany
	@JoinColumn(name="CustId")
	private List<ReservationDetails> bookingDetails;
	
	
	
}
