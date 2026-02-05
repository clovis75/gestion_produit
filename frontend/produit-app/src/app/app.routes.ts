import { Routes } from '@angular/router';
import {LoginComponent} from './Auth/login/login.component';
import {RegisterComponent} from './Auth/register/register.component';
import {HomeComponent} from './components/home/home.component';
import {authGuard} from './guards/auth.guard';

export const routes: Routes = [
  { path : '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'home', component: HomeComponent, canActivate : [authGuard] },
];
