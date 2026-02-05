import { Component } from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {AuthService} from '../auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    RouterLink,
    NgIf,
    FormsModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {

  userData = {
    firstname: '',
    lastname: '',
    email: '',
    password: ''
  };
  confirmPassword = '';
  errorMessage = '';
  successMessage = '';
  isLoading = false;

  constructor(private authService: AuthService, private router: Router) {
  }

  onSubmit() {
    // Réinitialiser les messages
    this.errorMessage = '';
    this.successMessage = '';

    // Validation
    if(!this.userData.firstname || !this.userData.lastname || !this.userData.email || !this.userData.password) {
      this.errorMessage = 'Tous les champs sont obligatoires';
      return;
    }

    if (this.userData.password !== this.confirmPassword) {
      this.errorMessage = 'Les mots de passe ne correspondent pas';
      return;
    }

    // Validation email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.userData.email)) {
      this.errorMessage = 'Veuillez entrer un email valide';
      return;
    }

    // Validation mot de passe
    if (this.userData.password.length < 6) {
      this.errorMessage = 'Le mot de passe doit contenir au moins 6 caractères';
      return;
    }

    // Activer le chargement
    this.isLoading = true;

    // Appel au service
    this.authService.register(this.userData).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.successMessage = 'Inscription réussie ! Redirection...';
        this.errorMessage = '';

        // Réinitialiser le formulaire
        this.userData = {
          firstname: '',
          lastname: '',
          email: '',
          password: ''
        };
        this.confirmPassword = '';

        // Redirection après 2 secondes
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (error) => {
        this.isLoading = false;

        // Gestion des erreurs plus détaillée
        if (error.status === 409) {
          this.errorMessage = 'Cet email est déjà utilisé';
        } else if (error.status === 400) {
          this.errorMessage = error.error?.message || 'Données invalides';
        } else if (error.status === 0) {
          this.errorMessage = 'Impossible de contacter le serveur';
        } else {
          this.errorMessage = error.error?.message || 'Erreur lors de l\'inscription';
        }

        this.successMessage = '';
      }
    });
  }
}
