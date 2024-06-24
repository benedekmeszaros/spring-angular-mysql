import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { User } from '../model/user';

const KEY = "jwt-token";
const RMM_KEY = "rmm-status"
const USER_KEY = "user-info";

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  private activeUserChanged = new BehaviorSubject<User | null>(this.safelyReadUser());
  activeUser: Observable<User | null> = this.activeUserChanged.asObservable();

  constructor() { }


  setToken(token: string): void {
    sessionStorage.setItem(KEY, token);
  }

  getToken(): string {
    return sessionStorage.getItem(KEY) ?? "N/A";
  }

  getRememberMe(): boolean {
    const remember = Number(localStorage.getItem(RMM_KEY)) ?? 0;
    return Boolean(remember);
  }

  setRememberMe(status: boolean): void {
    localStorage.setItem(RMM_KEY, status ? '1' : '0');
  }

  getUser(): User {
    return JSON.parse(sessionStorage.getItem(USER_KEY) ?? '');
  }

  setUser(user: User): void {
    this.activeUserChanged.next(user);
    sessionStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  clearStorage(session: boolean = true): void {
    if (session) {
      this.activeUserChanged.next(null);
      sessionStorage.clear();
    }
    else
      localStorage.clear();
  }

  private safelyReadUser(): User | null {
    const json = sessionStorage.getItem(USER_KEY);
    if (json)
      return JSON.parse(json);
    else
      return null;
  }
}
