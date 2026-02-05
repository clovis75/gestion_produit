import { Component } from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {AuthService} from '../auth.service';
import {response} from 'express';

@Component({
  selector: 'app-login',
  standalone : true,
  imports: [
    RouterLink,
    NgIf,
    FormsModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  credentials = {
    email: '',
    password: ''
  };
  errorMessage = '';
  constructor(private authService: AuthService, private router: Router) {}
  onSubmit() {
    //validation
    if (!this.credentials.email || !this.credentials.password) {
      this.errorMessage = "Tous les champs sont obligatoire";
      return;
    }

    // appel au service
    this.authService.login(this.credentials).subscribe({
      next : (response) => {
        console.log( 'connexion réussie', response);
        this.router.navigate(['/home']);
      },
      error : (error) => {
        this.errorMessage = error.error?.message || 'Email ou mot de passe incorrect';
      }
    });
  }
}
