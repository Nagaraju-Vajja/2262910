package com.task.domain.mytask.signup;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class SignupService {

	@Autowired
	private UnverifiedUserRepository unverifieduser;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	SignupRepository signupUser;

	public ResponseDto signup(SignupDto signupdto) throws MessagingException {

		String otp = generateOtp();
		UnverifiedUser UnverifiedUser = new UnverifiedUser();

		UnverifiedUser.setUserName(signupdto.getUserName());
		UnverifiedUser.setEmail(signupdto.getEmail());
		UnverifiedUser.setPhoneNo(signupdto.getPhone());

		UnverifiedUser.setUserName(signupdto.getUserName());
		UnverifiedUser.setPassword(signupdto.getPassword());
		UnverifiedUser.setOtp(otp);

		UnverifiedUser user = unverifieduser.save(UnverifiedUser);

		sendOtpEmail(user.getEmail(), otp);

		ResponseDto responsedto = new ResponseDto();
		responsedto.setResponse("OTP sent to email. Please Verify");
		return responsedto;

	}

	private void sendOtpEmail(String email, String otp) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(email);
		helper.setSubject("Your OTP Code");
		helper.setText("Your OTP is: " + otp);

		mailSender.send(message);
	}

	private String generateOtp() {
		Random random = new Random();
		return String.format("%04d", random.nextInt(10000));
	}

	public ResponseDto verifyOtpAndSaveUser(String providedOtp) {

		UnverifiedUser unverifiedUser = unverifieduser.findByEmail().get(0);

		if (unverifiedUser != null && unverifiedUser.getOtp().equals(providedOtp)) {

			SignupUser user = new SignupUser();
			user.setUserName(unverifiedUser.getUserName());
			user.setPassword(unverifiedUser.getPassword());
			user.setEmail(unverifiedUser.getEmail());
			user.setPhone(unverifiedUser.getPhoneNo());

			signupUser.save(user);

			unverifieduser.delete(unverifiedUser);

			ResponseDto responsedto = new ResponseDto();
			responsedto.setResponse("User registered successfully.");

			return responsedto;
		} else {
			ResponseDto responsedto = new ResponseDto();
			responsedto.setResponse("User not found. Invalid OTP.");
			return responsedto;

		}
	}

}
