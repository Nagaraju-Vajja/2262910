// additional-login.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AdditionalLoginService {
  private loginUrl = 'https://run.mocky.io/v3/4dbed952-2618-4085-859c-2cc25fb68fc9'; // Replace with your actual additional login URL

  constructor(private http: HttpClient) {}

  performAdditionalLogin(username: string, password: string): Observable<any> {
    return this.http.post(this.loginUrl, { username, password });
  }
}
