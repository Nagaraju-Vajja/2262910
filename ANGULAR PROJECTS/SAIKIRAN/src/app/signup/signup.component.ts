// signup.component.ts
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../auth/auth.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
})
export class SignupComponent implements OnInit {
  signupForm!: FormGroup;
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router, private http: HttpClient, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.signupForm = this.fb.group({
      firstName: ['', Validators.required],
      middleName: [''],
      lastName: ['', Validators.required],
      userName: ['', Validators.required],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required],
    });
  }

  signup() {
    if (this.signupForm.valid && this.signupForm.get('password')?.value === this.signupForm.get('confirmPassword')?.value) {
      this.authService.signup(this.signupForm.get('username')?.value, this.signupForm.get('password')?.value).subscribe(
        (success: boolean) => {
          if (success) {
            
            console.log('Signup successful');
            this.router.navigate(['/login']);
          } else {
            this.errorMessage = 'Signup failed. Please try again later.';
          }
        },
        (error) => {
          console.error('Signup failed:', error);
          this.errorMessage = 'Signup failed. Please try again later.';
        }
      );
    } else {
      this.errorMessage = 'Please fill in all required fields and make sure the passwords match.';
    }
  }
  
  private passwordsMatch(): boolean {
    return this.signupForm.get('password')?.value === this.signupForm.get('confirmPassword')?.value;
  }
  
}
