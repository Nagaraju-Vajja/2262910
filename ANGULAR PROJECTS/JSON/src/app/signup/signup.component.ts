import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  signupForm: FormGroup;

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.signupForm = this.fb.group({
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
      dob: ['', Validators.required],
      username: ['', Validators.required]
    }, { validators: this.customValidator });
  }

  onSubmit() {
    if (this.signupForm.valid) {
      const newData = {
        firstname: this.signupForm.value.firstname,
        lastname: this.signupForm.value.lastname,
        'date of birth': this.signupForm.value.dob,
        username: this.signupForm.value.username
      };

      const newDataArray = { data: [newData] };

      this.http.put('http://localhost:3000/saveData', newDataArray).subscribe(
        (response: any) => {
          console.log('Message from server:', response.message);
        },
        (error) => {
          console.error('Error:', error);
          console.error('Server-side error:', error.error);
        }
      );
    } else {
      this.signupForm.markAllAsTouched();
    }
  }

  // Custom validator to check if any field is empty
  customValidator(control: AbstractControl): { [key: string]: boolean } | null {
    const values = Object.values(control.value);
    const anyFieldEmpty = values.some(value => value === '' || value === null);

    return anyFieldEmpty ? { 'requiredFields': true } : null;
  }
}
