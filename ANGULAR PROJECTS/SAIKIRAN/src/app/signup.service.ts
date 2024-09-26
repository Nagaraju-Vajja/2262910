// signup.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SignupService {
  private apiUrl = 'https://run.mocky.io/v3/25ba3892-05be-4d2b-b57b-66c127c98865';

  constructor(private http: HttpClient) {}

  signup(formData: any): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });

    return this.http.post(this.apiUrl, formData, { headers });
  }
}