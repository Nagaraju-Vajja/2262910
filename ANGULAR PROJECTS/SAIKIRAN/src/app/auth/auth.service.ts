// auth.service.ts
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ResetPasswordService } from '../reset-password.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private storedCredentialsKey = 'userCredentials';

  constructor(private resetPasswordService: ResetPasswordService) {}

  signup(username: string, password: string): Observable<boolean> {
    // Store user credentials in local storage
    localStorage.setItem(this.storedCredentialsKey, JSON.stringify({ username, password }));
    return of(true);
  }

  login(username: string, password: string): Observable<boolean> {
    // Retrieve stored credentials from local storage
    const storedCredentials = localStorage.getItem(this.storedCredentialsKey);

    try {
      if (storedCredentials) {
        const storedData = JSON.parse(storedCredentials);

        // Ensure both username and password are present in stored data
        if (storedData && 'username' in storedData && 'password' in storedData) {
          const storedUsername = storedData['username'];
          const storedPassword = storedData['password'];

          // Perform a secure comparison of passwords
          if (this.secureCompare(password, storedPassword) && username === storedUsername) {
            return of(true);
          }
        }
      }
    } catch (error) {
      console.error('Error parsing stored credentials:', error);
    }

    return of(false);
  }
  private secureCompare(a: string, b: string): boolean {
    const maxLength = Math.max(a.length, b.length);
    let result = 0;

    for (let i = 0; i < maxLength; ++i) {
      const charCodeA = i < a.length ? a.charCodeAt(i) : 0;
      const charCodeB = i < b.length ? b.charCodeAt(i) : 0;

      result |= charCodeA ^ charCodeB;
    }

    return result === 0;
  }

  isAuthenticated(): Observable<boolean> {
    // Check if there are stored credentials
    return of(localStorage.getItem(this.storedCredentialsKey) !== null);
  }

  logout(): void {
    // Remove stored credentials on logout
    localStorage.removeItem(this.storedCredentialsKey);
  }

  // Add a method to store the token in local storage
  saveAuthToken(token: string): void {
    localStorage.setItem('authToken', token);
     
  }

  // Add a method to get the stored token
  getAuthToken(): string | null {
    return localStorage.getItem('authToken');
  }

  sendResetEmail(email: string): Observable<any> {
    // Delegate the responsibility to the ResetPasswordService
    return this.resetPasswordService.sendResetEmail(email);
  }
}
