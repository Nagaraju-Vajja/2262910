package com.example.demo.Controller;
import com.example.demo.Models.SignUp;
import com.example.demo.Repository.SignupRepo;
import com.example.demo.SignupDto.SignupDto;
import com.example.demo.services.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class SignupController {

   @Autowired
    private SignupRepo signupRepo;

   @Autowired
   private SignupService signupService;

    @PostMapping("/add")
    public ResponseEntity<String> Signup(@RequestBody SignupDto signupDto) {

        SignUp signUp = new SignUp();
        signUp.setFirstName(signupDto.getFirstName());
        signUp.setLastName(signupDto.getLastName());
        signUp.setDob(signupDto.getDob());
        signUp.setUserName(signupDto.getUserName());
        System.out.println("signUp"+signUp);
        signupRepo.save(signUp);
        return ResponseEntity.ok("Signup Successfully...");
    }

    @PostMapping("/getsignup")
    public List<SignupDto> getSignupDetails() {
        List<SignUp> result = signupService.getSignupDetails();
        List<SignupDto> dtos = new ArrayList<>();

        for (SignUp obj : result) {
            SignupDto dto = new SignupDto();
            dto.setFirstName(obj.getFirstName());
            dto.setLastName(obj.getLastName());
            dto.setDob(obj.getDob());
            dto.setUserName(obj.getUserName());
            dtos.add(dto);
        }
        return dtos;
    }
}

