import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../model/user';
import { AppSettings } from '../setting/app.settings';
import { StorageService } from './storage.service';

const BASE_URL = AppSettings.API_URL + "/users";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(
    private http: HttpClient,
    private storage: StorageService
  ) { }

  getMe(): Observable<User> {
    return this.http.get<User>(
      `${BASE_URL}/me`,
      { headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }

  updateAvatar(image: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', image);

    return this.http.patch(
      `${BASE_URL}/avatar`,
      formData,
      {
        responseType: "text",
        headers: new HttpHeaders({
          'Authorization': `Bearer ${this.storage.getToken()}`
        })
      }
    );
  }

  updateUsername(username: string): Observable<User> {
    return this.http.patch<User>(
      `${BASE_URL}/username`,
      { username },
      { headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    )
  }

  updatePassword(password: string): Observable<string> {
    return this.http.patch(
      `${BASE_URL}/password`,
      { password },
      { responseType: "text", headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    )
  }

  suspend(): Observable<string> {
    return this.http.delete(
      `${BASE_URL}/me`,
      { responseType: "text", headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }
}
