import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Task } from '../model/task';
import { AppSettings } from '../setting/app.settings';
import { StorageService } from './storage.service';

const BASE_URL = (boardId: number, phaseId: number): string => `${AppSettings.API_URL}/boards/${boardId}/phases/${phaseId}/tasks`;

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  constructor(
    private http: HttpClient,
    private storage: StorageService
  ) { }

  update(boardId: number, phaseId: number, taskId: number, label: string, description: string): Observable<Task> {
    return this.http.patch<Task>(
      `${BASE_URL(boardId, phaseId)}/${taskId}`,
      { label, description },
      { headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }

  move(boardId: number, oldPhaseId: number, taskId: number, newPhaseId: number, pos: number): Observable<string> {
    return this.http.put(
      `${BASE_URL(boardId, oldPhaseId)}/${taskId}?otherPhaseId=${newPhaseId}&position=${pos}`,
      {},
      { responseType: "text", headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }

  upload(boardId: number, phaseId: number, label: string, description: string): Observable<Task> {
    return this.http.post<Task>(
      BASE_URL(boardId, phaseId),
      { label, description },
      { headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }

  delete(boardId: number, oldPhaseId: number, taskId: number): Observable<string> {
    return this.http.delete(
      `${BASE_URL(boardId, oldPhaseId)}/${taskId}`,
      { responseType: "text", headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }
}
