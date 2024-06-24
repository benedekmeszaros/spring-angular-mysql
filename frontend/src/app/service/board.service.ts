import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Board } from '../model/board';
import { AppSettings } from '../setting/app.settings';
import { StorageService } from './storage.service';

const BASE_URL: string = AppSettings.API_URL + "/boards";

@Injectable({
  providedIn: 'root'
})
export class BoardService {

  constructor(
    private http: HttpClient,
    private storage: StorageService
  ) { }

  getBoards(): Observable<Board[]> {
    return this.http.get<Board[]>(
      BASE_URL,
      { headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }

  getBoard(id: number): Observable<Board> {
    return this.http.get<Board>(
      `${BASE_URL}/${id}`,
      { headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }

  upload(title: string, description: string): Observable<number> {
    return this.http.post<number>(
      BASE_URL,
      { title, description },
      { headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }

  update(id: number, title: string, description: string): Observable<Board> {
    return this.http.patch<Board>(
      `${BASE_URL}/${id}`,
      { title, description },
      { headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }

  remove(id: number): Observable<string> {
    return this.http.delete(
      `${BASE_URL}/${id}`,
      { responseType: "text", headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }
}
