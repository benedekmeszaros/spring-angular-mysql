import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Phase } from '../model/phase';
import { AppSettings } from '../setting/app.settings';
import { StorageService } from './storage.service';

const BASE_URL = (boardId: number): string => `${AppSettings.API_URL}/boards/${boardId}/phases`;

@Injectable({
  providedIn: 'root'
})
export class PhaseService {

  constructor(
    private http: HttpClient,
    private storage: StorageService
  ) { }

  upload(boardId: number, label: string, description: string): Observable<Phase> {
    return this.http.post<Phase>(
      BASE_URL(boardId),
      { label, description },
      { headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }

  update(boardId: number, phaseId: number, label: string, description: string): Observable<Phase> {
    return this.http.patch<Phase>(
      `${BASE_URL(boardId)}/${phaseId}`,
      { label, description },
      { headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }

  move(boardId: number, phaseId: number, pos: number): Observable<string> {
    return this.http.put(
      `${BASE_URL(boardId)}/${phaseId}?position=${pos}`,
      {},
      { responseType: "text", headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }

  remove(boardId: number, phaseId: number): Observable<string> {
    return this.http.delete(
      `${BASE_URL(boardId)}/${phaseId}`,
      { responseType: "text", headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }
}
