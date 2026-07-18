import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Task } from '../models/models';

@Injectable({ providedIn: 'root' })
export class TaskService {

  private readonly baseUrl = `${environment.apiUrl}/tasks`;

  constructor(private http: HttpClient) { }

  claim(task: Task): Observable<Task>    { return this.action(task, 'claim'); }
  release(task: Task): Observable<Task>  { return this.action(task, 'release'); }
  complete(task: Task): Observable<Task> { return this.action(task, 'complete'); }
  approve(task: Task): Observable<Task>  { return this.action(task, 'approve'); }
  reject(task: Task): Observable<Task>   { return this.action(task, 'reject'); }

  private action(task: Task, verb: string): Observable<Task> {
    return this.http.post<Task>(`${this.baseUrl}/${task.id}/${verb}`,
      { version: task.version });
  }
}