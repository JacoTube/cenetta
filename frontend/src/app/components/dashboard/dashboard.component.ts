import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { EventService } from '../../core/event.service';
import { EventSummary } from '../../models/models';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  events: EventSummary[] = [];
  error: string | null = null;

  title = '';
  description = '';
  eventDateTime = '';
  deadline = '';
  eventPassword = '';

  inviteCode = '';
  joinPassword = '';

  constructor(
    private eventService: EventService,
    private auth: AuthService,
    private router: Router) { }

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.eventService.getMyEvents().subscribe({
      next: list => this.events = list,
      error: () => this.error = 'Impossibile caricare gli eventi'
    });
  }

  createEvent(): void {
    this.error = null;
    this.eventService.createEvent({
      title: this.title,
      description: this.description,
      eventDateTime: this.eventDateTime,
      deadline: this.deadline,
      eventPassword: this.eventPassword
    }).subscribe({
      next: () => {
        this.resetCreateForm();
        this.reload();
      },
      error: err => this.error = err.error?.message ?? 'Creazione non riuscita'
    });
  }

  joinEvent(): void {
    this.error = null;
    this.eventService.joinEvent(this.inviteCode, this.joinPassword).subscribe({
      next: () => {
        this.inviteCode = '';
        this.joinPassword = '';
        this.reload();
      },
      error: err => this.error = err.error?.message ?? 'Accesso non riuscito'
    });
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }

  private resetCreateForm(): void {
    this.title = '';
    this.description = '';
    this.eventDateTime = '';
    this.deadline = '';
    this.eventPassword = '';
  }
}