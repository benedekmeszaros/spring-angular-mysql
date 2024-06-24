import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Invitation } from '../model/invitation';
import { Membership } from '../model/membership';
import { AppSettings } from '../setting/app.settings';
import { StorageService } from './storage.service';

const BASE_URL = AppSettings.API_URL + "/invitations";

@Injectable({
  providedIn: 'root'
})
export class MemberService {

  constructor(
    private http: HttpClient,
    private storage: StorageService
  ) { }

  getInvitations(): Observable<Invitation[]> {
    return this.http.get<Invitation[]>(
      BASE_URL,
      { headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }

  invite(boardId: number, email: string, access: string): Observable<String> {
    return this.http.post(
      `${BASE_URL}/boards/${boardId}`,
      { email, access },
      { responseType: "text", headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }

  accept(boardId: number, invitationId: number): Observable<Membership> {
    return this.http.patch<Membership>(
      `${BASE_URL}/boards/${boardId}?invitationId=${invitationId}&isAccepted=true`,
      {},
      { headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }

  deny(boardId: number, invitationId: number): Observable<string> {
    return this.http.patch(
      `${BASE_URL}/boards/${boardId}?invitationId=${invitationId}&isAccepted=false`,
      {},
      { responseType: "text", headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }

  update(boardId: number, memberId: number, access: string): Observable<string> {
    return this.http.patch(
      `${AppSettings.API_URL}/boards/${boardId}/members/${memberId}?access=${access}`,
      {},
      { responseType: "text", headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }

  remove(boardId: number, memberId: number): Observable<string> {
    return this.http.delete(
      `${AppSettings.API_URL}/boards/${boardId}/members/${memberId}`,
      { responseType: "text", headers: AppSettings.getBearerHeaders(this.storage.getToken()) }
    );
  }
}
