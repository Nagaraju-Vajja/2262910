
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  userName: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(private router: Router, private authService: AuthService) {}

  login() {
    if (this.isFormValid()) {
      this.authService.login(this.userName, this.password).subscribe(
        (success: boolean) => {
          if (success) {
            console.log('Login successful');
            this.router.navigate(['/employee-form']);
          } else {
            this.errorMessage = 'Login failed. Please check your credentials.';
          }
        },
        (error) => {
          console.error('Login failed:', error);
          this.errorMessage = 'Login failed. Please check your credentials.';
        }
      );
    } else {
      this.errorMessage = 'Please provide both username and password.';
    }
  }

  private isFormValid(): boolean {
    return this.userName.trim() !== '' && this.password.trim() !== '';
  }
}
