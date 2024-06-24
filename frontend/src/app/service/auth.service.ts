import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';
import { AppSettings } from '../setting/app.settings';
import { StorageService } from './storage.service';

const BASE_URL: string = AppSettings.API_URL + "/auth";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(
    private http: HttpClient,
    private storage: StorageService
  ) { }

  isAuthenticated(): boolean {
    return this.storage.getToken() != "N/A";
  }

  login(email: string, password: string): Observable<string> {
    return this.http.post(
      `${BASE_URL}/login`,
      { email, password },
      { withCredentials: true, responseType: "text" }
    ).pipe(
      map(res => {
        this.storage.setToken(res);
        return res;
      }),
      catchError(err => {
        return throwError(() => err);
      })
    );
  }

  logout(): Observable<string> {
    return this.http.post(
      `${BASE_URL}/logout`,
      {},
      {
        withCredentials: true,
        responseType: "text",
        headers: AppSettings.getBearerHeaders(this.storage.getToken())
      }
    ).pipe(
      map(message => {
        this.storage.clearStorage();
        this.storage.setRememberMe(false);
        return message;
      }),
      catchError(err => {
        this.storage.clearStorage();
        return throwError(() => err);
      })
    );
  }

  registrate(email: string, username: string, password: string): Observable<string> {
    return this.http.post(
      `${BASE_URL}/register`,
      { email, fullName: username, password },
      { responseType: "text" }
    );
  }

  refresh(): Observable<string> {
    return this.http.get(
      `${BASE_URL}/refresh`,
      {
        withCredentials: true,
        responseType: "text",
        headers: AppSettings.getBearerHeaders(this.storage.getToken())
      }
    ).pipe(
      map(token => {
        this.storage.setToken(token);
        return token;
      }),
      catchError(err => {
        return throwError(() => err);
      })
    );

  }
}
