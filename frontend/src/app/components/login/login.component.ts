import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  username = '';
  password = '';
  error: string | null = null;

  constructor(private auth: AuthService, private router: Router) { }

  submit(): void {
    this.auth.login(this.username, this.password).subscribe({
      next: () => this.router.navigate(['/eventi']),
      error: () => this.error = 'Credenziali non valide'
    });
  }
}