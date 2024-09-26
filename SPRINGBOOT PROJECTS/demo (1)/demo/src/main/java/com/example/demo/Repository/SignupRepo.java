package com.example.demo.Repository;

import com.example.demo.Models.SignUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignupRepo extends JpaRepository<SignUp, Integer> {
    

}
