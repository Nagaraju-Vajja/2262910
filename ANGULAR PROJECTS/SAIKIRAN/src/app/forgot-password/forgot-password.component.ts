
import { Component } from '@angular/core';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css'],
})
export class ForgotPasswordComponent {
  email: string = '';
  successMessage: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService) {}

  sendResetEmail() {
    this.authService.sendResetEmail(this.email).subscribe(
      (response: any) => {
        console.log('Reset email sent successfully:', response);
        this.successMessage = 'Reset email sent successfully. Check your email for instructions.';
      },
      (error: any) => {
        console.error('Failed to send reset email:', error);
        this.errorMessage = 'Failed to send reset email. Please check the email address.';
      }
    );
  }
}
