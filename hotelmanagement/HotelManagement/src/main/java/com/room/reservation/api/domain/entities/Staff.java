package com.room.reservation.api.domain.entities;

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
public class Staff {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int staffId;

	private String firstName;
	private String lastName;
	private String phoneNo;
	private Date hireDate;
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "staffId")
	private List<ReservationDetails> bookingdetails;

	
}
