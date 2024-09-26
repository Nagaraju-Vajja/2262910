package com.example.demo.services;

import com.example.demo.Models.SignUp;
import com.example.demo.Repository.SignupRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SignupService {

    @Autowired
    private SignupRepo signupRepo;

    public List<SignUp> getSignupDetails() {
        List<SignUp> signup = signupRepo.findAll();
        List<SignUp> filters = new ArrayList<>();

        for (SignUp signupfilter : signup) {
            try {
                String userName = signupfilter.getUserName();
                if (userName != null && !userName.isEmpty()) {
                    filters.add(signupfilter);
                }
            } catch (Exception e) {
                // Log the exception or rethrow it if needed
                System.err.println("Error processing signup: " + e.getMessage());
            }
        }
        return filters;
    }
}