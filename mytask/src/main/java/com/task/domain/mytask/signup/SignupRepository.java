package com.task.domain.mytask.signup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  SignupRepository  extends JpaRepository<SignupUser, Integer>{
	
	

}
