import { Component, OnInit } from '@angular/core';
import { SignupService } from '../singup.service';
import { SignupDto } from '../signup-dto.model';
@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {
  userList !: SignupDto[];

  constructor(private signupService: SignupService) { }

  ngOnInit(): void {
    this.loadSignupDetails();
  }

  loadSignupDetails() {
    this.signupService.getSignupDetails().subscribe(
      data => {
        this.userList = data;
      },
      error => {
        console.error('Error fetching signup details: ', error);
      }
    );
  }
}
