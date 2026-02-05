import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable, tap} from 'rxjs';
import { Router } from '@angular/router';
import { LoginResponse } from '../models/loginResponse.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl:string = '/api/auth';
  private currentUserSubject : BehaviorSubject<any>;
  public currentUser : Observable<any>;
  constructor(private http: HttpClient, private router : Router) {
    const storedUser = localStorage.getItem('currentUser');
    this.currentUserSubject = new BehaviorSubject<any>(
      storedUser ? JSON.parse(storedUser) : null
    );
    this.currentUser = this.currentUserSubject.asObservable();
  }
  public get currentUserValue(){
    return  this.currentUserSubject.value;
  }
  //inscription
  register(userDate: any):Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, userDate);
  }
  //connexion
  login(credentials: any): Observable<any>{
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, credentials).pipe(
      tap(response => {
        localStorage.setItem('currentUser', JSON.stringify(response));
        localStorage.setItem('token', response.token);
        this.currentUserSubject.next(response);
      })
    );
  }
  // deconnexion
  logout() : void {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
    this.router.navigate(['./login']);
  }
  // verifier si l'utilisateur est deconnecter
  isLoggerIn(): boolean {
    return this.currentUserValue !== null;
  }
}
