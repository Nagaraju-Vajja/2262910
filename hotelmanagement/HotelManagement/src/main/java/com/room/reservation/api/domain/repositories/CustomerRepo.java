package com.room.reservation.api.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.room.reservation.api.domain.entities.Customer;

@Repository
public interface  CustomerRepo  extends JpaRepository<Customer, Integer>{

	
}
