// signup.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SignupDto } from './signup-dto.model';

@Injectable({
  providedIn: 'root',
})
export class SignupService {
  private apiUrl = 'http://localhost:8083/api/add';
  private apiUrl2 = 'http://localhost:8083/api/getsignup';

  constructor(private http: HttpClient) {}

  addUser(user: any): Observable<any> {
    return this.http.post(this.apiUrl, user);
  }
  
    getSignupDetails(): Observable<SignupDto[]> {
      return this.http.get<SignupDto[]>(this.apiUrl2);
    }
}































