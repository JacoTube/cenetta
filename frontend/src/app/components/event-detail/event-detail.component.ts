import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EventService } from '../../core/event.service';
import { TaskService } from '../../core/task.service';
import { AuthService } from '../../core/auth.service';
import { EventDetail, Task, TaskStatus } from '../../models/models';

@Component({
  selector: 'app-event-detail',
  templateUrl: './event-detail.component.html'
})
export class EventDetailComponent implements OnInit {

  event: EventDetail | null = null;

  // form "aggiungi task"
  newTitle = '';
  newNote = '';

  // feedback all'utente
  message: string | null = null;
  messageKind: 'ok' | 'warn' | 'error' = 'ok';

  /** id delle task su cui e' in corso una richiesta: disabilita i pulsanti */
  busy: number[] = [];

  private eventId!: number;

  constructor(
    private route: ActivatedRoute,
    private eventService: EventService,
    private taskService: TaskService,
    private auth: AuthService) { }

  ngOnInit(): void {
    this.eventId = Number(this.route.snapshot.paramMap.get('id'));
    this.reload();
  }

  reload(): void {
    this.eventService.getEvent(this.eventId).subscribe({
      next: e => this.event = e,
      error: err => this.mostra('error', err.error?.message ?? 'Evento non accessibile')
    });
  }

  // ------------------------------------------------------------------
  // Raggruppamenti per stato (usati dal template)
  // ------------------------------------------------------------------

  private byStatus(s: TaskStatus): Task[] {
    return this.event ? this.event.tasks.filter(t => t.status === s) : [];
  }

  get pending()   { return this.byStatus('PENDING_APPROVAL'); }
  get free()      { return this.byStatus('FREE'); }
  get assigned()  { return this.byStatus('ASSIGNED'); }
  get completed() { return this.byStatus('COMPLETED'); }
  get rejected()  { return this.byStatus('REJECTED'); }

  /** Percentuale di completamento, escludendo proposte e rifiutate. */
  get progress(): number {
    const totali = this.free.length + this.assigned.length + this.completed.length;
    return totali === 0 ? 0 : Math.round((this.completed.length / totali) * 100);
  }

  /** Vero se la task e' assegnata all'utente loggato. */
  isMine(t: Task): boolean {
    return t.assignee?.id === this.auth.currentUser?.id;
  }

  isBusy(t: Task): boolean {
    return this.busy.includes(t.id);
  }

  // ------------------------------------------------------------------
  // Azioni
  // ------------------------------------------------------------------

  addTask(): void {
    if (!this.newTitle.trim()) { return; }

    this.eventService.addTask(this.eventId, this.newTitle.trim(), this.newNote.trim())
      .subscribe({
        next: task => {
          this.newTitle = '';
          this.newNote = '';
          this.mostra('ok', task.status === 'PENDING_APPROVAL'
            ? 'Proposta inviata: attende l\'approvazione dell\'organizzatore.'
            : 'Task aggiunta alla checklist.');
          this.reload();
        },
        error: err => this.gestisciErrore(err)
      });
  }

  claim(t: Task)    { this.esegui(t, this.taskService.claim(t),    'Task presa in carico.'); }
  release(t: Task)  { this.esegui(t, this.taskService.release(t),  'Task rilasciata: e\' di nuovo libera.'); }
  complete(t: Task) { this.esegui(t, this.taskService.complete(t), 'Task completata.'); }
  approve(t: Task)  { this.esegui(t, this.taskService.approve(t),  'Proposta approvata: la task e\' ora libera.'); }
  reject(t: Task)   { this.esegui(t, this.taskService.reject(t),   'Proposta rifiutata.'); }

  /**
   * Esegue un'azione sulla task. Dopo ogni operazione ricarichiamo l'evento
   * anziche' aggiornare la lista in locale: cosi' la vista riflette sempre lo
   * stato reale del server, comprese le modifiche fatte da altri partecipanti.
   */
  private esegui(t: Task, call$: Observable<Task>, successo: string): void {
    this.busy = [...this.busy, t.id];

    call$.subscribe({
      next: () => {
        this.busy = this.busy.filter(id => id !== t.id);
        this.mostra('ok', successo);
        this.reload();
      },
      error: err => {
        this.busy = this.busy.filter(id => id !== t.id);
        this.gestisciErrore(err);
      }
    });
  }

  /**
   * Traduce gli errori HTTP del backend in feedback per l'utente.
   *
   * 409 CONFLICT — conflitto di concorrenza rilevato dal locking ottimistico:
   *   un altro partecipante ha modificato la task fra il momento in cui questo
   *   client l'ha letta e quello in cui ha agito. Mostriamo il messaggio e
   *   ricarichiamo, cosi' l'utente vede subito com'e' cambiata la situazione.
   *
   * 423 LOCKED — la deadline dell'evento e' trascorsa: congelamento passivo.
   *   Ricarichiamo perche' la vista deve passare in sola lettura.
   */
  private gestisciErrore(err: HttpErrorResponse): void {
    const testo = err.error?.message ?? 'Operazione non riuscita.';

    if (err.status === 409 || err.status === 423) {
      this.mostra('warn', testo);
      this.reload();
      return;
    }
    this.mostra('error', testo);
  }

  private mostra(kind: 'ok' | 'warn' | 'error', testo: string): void {
    this.messageKind = kind;
    this.message = testo;
  }

  chiudiMessaggio(): void {
    this.message = null;
  }
}