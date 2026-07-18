import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { EventDetail, EventSummary, Task } from '../models/models';

@Injectable({ providedIn: 'root' })
export class EventService {

  private readonly baseUrl = `${environment.apiUrl}/events`;

  constructor(private http: HttpClient) { }

  getMyEvents(): Observable<EventSummary[]> {
    return this.http.get<EventSummary[]>(this.baseUrl);
  }

  getEvent(id: number): Observable<EventDetail> {
    return this.http.get<EventDetail>(`${this.baseUrl}/${id}`);
  }

  createEvent(payload: {
    title: string; description: string;
    eventDateTime: string; deadline: string; eventPassword: string;
  }): Observable<EventDetail> {
    return this.http.post<EventDetail>(this.baseUrl, payload);
  }

  joinEvent(inviteCode: string, eventPassword: string): Observable<EventDetail> {
    return this.http.post<EventDetail>(`${this.baseUrl}/join`,
      { inviteCode, eventPassword });
  }

  addTask(eventId: number, title: string, note: string): Observable<Task> {
    return this.http.post<Task>(`${this.baseUrl}/${eventId}/tasks`, { title, note });
  }
}