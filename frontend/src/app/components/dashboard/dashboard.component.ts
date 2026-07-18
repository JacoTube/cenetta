import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { EventService } from '../../core/event.service';
import { EventSummary, UserDto } from '../../models/models';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {

  events: EventSummary[] = [];
  error: string | null = null;

  showCreate = false;
  showJoin = false;

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

  get currentUser(): UserDto | null {
    return this.auth.currentUser;
  }

  get upcoming(): EventSummary[] {
    return this.events.filter(e => !e.closed);
  }

  get past(): EventSummary[] {
    return this.events.filter(e => e.closed);
  }

  reload(): void {
    this.eventService.getMyEvents().subscribe({
      next: list => this.events = list,
      error: () => this.error = 'Impossibile caricare gli eventi.'
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
      next: creato => this.router.navigate(['/eventi', creato.id]),
      error: err => this.error = err.error?.message ?? 'Creazione non riuscita.'
    });
  }

  joinEvent(): void {
    this.error = null;
    this.eventService.joinEvent(this.inviteCode, this.joinPassword).subscribe({
      next: evento => this.router.navigate(['/eventi', evento.id]),
      error: err => this.error = err.error?.message ?? 'Accesso all\'evento non riuscito.'
    });
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}