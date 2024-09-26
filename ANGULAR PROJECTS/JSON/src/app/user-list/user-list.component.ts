import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {
  userList !: any[];

  constructor(private http: HttpClient) { }

  ngOnInit() {
    this.fetchUserData();
  }

  fetchUserData() {
    this.http.get('http://localhost:3000/getData').subscribe(
      (response: any) => {
        this.userList = response.data;
      },
      (error) => {
        console.error('Error:', error);
      }
    );
  }
}
