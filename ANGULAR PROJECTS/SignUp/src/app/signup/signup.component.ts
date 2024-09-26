import { Component } from '@angular/core';
import { SignupService } from '../singup.service';
import { User } from '../user';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
})
export class SignupComponent {
  user: User = {
    firstName: '',
    lastName: '',
    dob: '',
    userName: '',
  };

  constructor(private signupService: SignupService) {}

  submitForm() {
    this.signupService.addUser(this.user).subscribe(
      (response) => {
        console.log('User added successfully', response);
        

        // Check if the response is a JSON object
        if (response instanceof Object) {
          // Handle JSON response
          console.log('JSON response:', response);
          // Add your logic for handling JSON response here
          console.log("response"+response);
        } else {
          console.log('Non-JSON response:', response);
          // Handle plain text response
          // Add your logic for handling plain text response here
        }
      },
      (error) => {
        console.error('Error adding user', error);
      }
    );
  }
}