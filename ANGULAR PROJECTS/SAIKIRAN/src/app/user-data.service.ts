import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UserDataService {
  private apiUrl = 'http://localhost:3000';

  constructor(private http: HttpClient) { }

  getUserData() {
    return this.http.get(`${this.apiUrl}/getData`);
  }
}
