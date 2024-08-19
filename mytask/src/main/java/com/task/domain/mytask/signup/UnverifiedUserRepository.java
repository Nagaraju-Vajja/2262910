package com.task.domain.mytask.signup;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UnverifiedUserRepository  extends JpaRepository<UnverifiedUser, Integer> {

	@Query("SELECT u FROM UnverifiedUser u ORDER BY u.id DESC")
	 List <UnverifiedUser> findByEmail();
}
