import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product.model';
import { ApiResponse } from '../models/apiResponse.model';
import {environment} from '../environnement/environnements.prod';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private baseUrl:string = `${environment.apiUrl}/api/produits`;

  constructor(private http: HttpClient) { }

  private getHeaders(): HttpHeaders {
    const user = JSON.parse(localStorage.getItem('currentUser') || '{}');
    const token = user?.token;
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  // Récupérer tous les produits
  getAllProducts(): Observable<ApiResponse<Product[]>> {
    return this.http.get<ApiResponse<Product[]>>(this.baseUrl, { headers: this.getHeaders() });
  }

  // Récupérer un produit par ID
  getProductById(id: number): Observable<ApiResponse<Product>> {
    return this.http.get<ApiResponse<Product>>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }

  // Créer un produit
  createProduct(product: Product): Observable<ApiResponse<Product>> {
    return this.http.post<ApiResponse<Product>>(this.baseUrl, product, { headers: this.getHeaders() });
  }

  // Mettre à jour un produit
  updateProduct(id: number, product: Product): Observable<ApiResponse<Product>> {
    return this.http.put<ApiResponse<Product>>(`${this.baseUrl}/${id}`, product, { headers: this.getHeaders() });
  }

  // Supprimer un produit
  deleteProduct(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }
}
