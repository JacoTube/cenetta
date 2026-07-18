import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, switchMap, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse, UserDto } from '../models/models';

const TOKEN_KEY = 'cenetta.token';
const USER_KEY  = 'cenetta.user';

@Injectable({ providedIn: 'root' })
export class AuthService {

  constructor(private http: HttpClient) { }

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/login`, { username, password })
      .pipe(tap(res => this.salvaSessione(res)));
  }

  register(username: string, password: string, displayName: string): Observable<AuthResponse> {
    return this.http
      .post(`${environment.apiUrl}/auth/register`, { username, password, displayName })
      .pipe(switchMap(() => this.login(username, password)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  }

  get token(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  get currentUser(): UserDto | null {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) as UserDto : null;
  }

  get isLoggedIn(): boolean {
    return this.token !== null;
  }

  private salvaSessione(res: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, res.token);
    localStorage.setItem(USER_KEY, JSON.stringify(res.user));
  }
}