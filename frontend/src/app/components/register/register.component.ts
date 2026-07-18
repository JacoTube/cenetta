import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html'
})
export class RegisterComponent {

  displayName = '';
  username = '';
  password = '';

  loading = false;
  error: string | null = null;

  constructor(private auth: AuthService, private router: Router) { }

  submit(): void {
    if (!this.displayName || !this.username || this.password.length < 6) {
      this.error = 'Compila tutti i campi. La password deve avere almeno 6 caratteri.';
      return;
    }
    this.loading = true;
    this.error = null;

    // register() registra e poi effettua il login, restituendo il token
    this.auth.register(this.username, this.password, this.displayName).subscribe({
      next: () => this.router.navigate(['/eventi']),
      error: err => {
        this.loading = false;
        this.error = err.error?.message ?? 'Registrazione non riuscita.';
      }
    });
  }
}