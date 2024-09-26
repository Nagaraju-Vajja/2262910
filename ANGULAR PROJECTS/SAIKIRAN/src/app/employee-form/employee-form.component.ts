import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-employee-form',
  templateUrl: './employee-form.component.html',
  styleUrls: ['./employee-form.component.css']
})
export class EmployeeFormComponent implements OnInit {
  employeeForm!: FormGroup;

  constructor(private fb: FormBuilder, private router: Router) { }

  ngOnInit() {
    this.employeeForm = this.fb.group({
      employeeId: ['', Validators.required],
      employeeName: ['', Validators.required],
      address: ['', Validators.required],
      dob: ['', Validators.required],
      gender: ['', Validators.required],
      occupation: ['', Validators.required]
    });
  }

   onSubmit() {
  //if (this.employeeForm.valid) {
     // Save data or perform any necessary actions
  //     this.router.navigate(['/employee-table'], { state: { data: this.employeeForm.value } });
  //   } else {
      // Form is invalid, show error or handle accordingly
  //   }
  //  }
}
}
