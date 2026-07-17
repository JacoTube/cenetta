import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EventDetail, EventSummary } from '../models/models';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class EventService {
  constructor(private http: HttpClient) { }

  getMyEvents(): Observable<EventSummary[]> {
    return this.http.get<EventSummary[]>(`${environment.apiUrl}/events`);
  }

  createEvent(payload: { title: string; description: string; eventDateTime: string; deadline: string; eventPassword: string; }): Observable<EventDetail> {
    return this.http.post<EventDetail>(`${environment.apiUrl}/events`, payload);
  }

  joinEvent(inviteCode: string, eventPassword: string): Observable<EventDetail> {
    return this.http.post<EventDetail>(`${environment.apiUrl}/events/join`, { inviteCode, eventPassword });
  }
}