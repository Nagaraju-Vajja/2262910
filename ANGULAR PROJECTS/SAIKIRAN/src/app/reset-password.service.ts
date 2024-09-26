
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ResetPasswordService {
  private resetUrl = 'https://run.mocky.io/v3/25ba3892-05be-4d2b-b57b-66c127c98865/reset-password';

  constructor(private http: HttpClient) {}

  sendResetEmail(email: string): Observable<any> {
    // Use the correct reset URL
    return this.http.post(this.resetUrl, { email });
  }
}
