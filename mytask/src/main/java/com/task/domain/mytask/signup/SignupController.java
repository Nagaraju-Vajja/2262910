package com.task.domain.mytask.signup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;

@RequestMapping("/api")
@RestController
public class SignupController {
	
	@Autowired
	private SignupService signupservice;
	
	@PostMapping("/signup")
	public ResponseEntity<ResponseDto> signup(@RequestBody SignupDto signupdto ) throws MessagingException
	{
		
		ResponseDto response =signupservice.signup(signupdto);
		
		return ResponseEntity.ok(response);
		
	}
	
	@PostMapping("/verifyotp")
	public ResponseEntity<ResponseDto> verifyOtp( @RequestParam("otp") String otp  )
	{
		
		ResponseDto message = signupservice.verifyOtpAndSaveUser(otp);
		
		return ResponseEntity.ok(message);
		
	}
	
}
