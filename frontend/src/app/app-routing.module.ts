import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { EventDetailComponent } from './components/event-detail/event-detail.component';
import { authGuard } from './core/auth.guard';

const routes: Routes = [
  { path: '',              pathMatch: 'full', redirectTo: 'eventi' },
  { path: 'login',         component: LoginComponent },
  { path: 'registrazione', component: RegisterComponent },
  { path: 'eventi',        component: DashboardComponent,   canActivate: [authGuard] },
  { path: 'eventi/:id',    component: EventDetailComponent, canActivate: [authGuard] },
  { path: '**',            redirectTo: 'eventi' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }