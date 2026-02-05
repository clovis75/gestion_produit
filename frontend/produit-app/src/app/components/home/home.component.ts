import {Component, OnInit} from '@angular/core';
import {DecimalPipe, NgFor, NgIf, SlicePipe} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Product} from '../../models/product.model';
import {ApiResponse} from '../../models/apiResponse.model';
import {AuthService} from '../../Auth/auth.service';
import {ProductService} from '../../services/product.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [NgFor, NgIf, DecimalPipe, SlicePipe, FormsModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  currentUser: any;
  products: Product[] = [];
  filteredProducts: Product[] = [];
  searchTerm: string = '';

  // Messages
  errorMessage = '';
  successMessage = '';
  isLoading = false;

  // Modal states
  showModal: boolean = false;
  showViewModal: boolean = false;
  isEditMode: boolean = false;

  currentProduct: Product = {
    nom: '',
    description: '',
    prix: 0,
    stock: 0,
  };

  selectedProduct: Product | null = null;

  constructor(
    private authService: AuthService,
    private productService: ProductService,
    private router: Router
  ) {
    this.currentUser = this.authService.currentUserValue;
  }

  ngOnInit() {
    if (!this.authService.isLoggerIn()) {
      this.router.navigate(['/login']);
      return;
    }
    this.loadProducts();
  }

  loadProducts() {
    this.productService.getAllProducts().subscribe({
      next: (response: ApiResponse<Product[]>) => {
        this.products = response.data;
        this.filteredProducts = response.data;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des produits', error);
        this.errorMessage = 'Erreur lors du chargement des produits';
        this.products = [];
        this.filteredProducts = [];
      }
    });
  }

  filterProducts() {
    if (!this.searchTerm) {
      this.filteredProducts = this.products;
      return;
    }

    const term = this.searchTerm.toLowerCase();
    this.filteredProducts = this.products.filter(product =>
      product.nom.toLowerCase().includes(term) ||
      product.description.toLowerCase().includes(term) ||
      product.stock.toString().includes(term) ||
      product.prix.toString().includes(term)
    );
  }

  // Stats methods
  getInStockCount(): number {
    return this.products.filter(p => p.stock > 10).length;
  }

  getLowStockCount(): number {
    return this.products.filter(p => p.stock > 0 && p.stock <= 10).length;
  }

  getTotalValue(): number {
    return this.products.reduce((sum, p) => sum + (p.prix * p.stock), 0);
  }

  // Modal methods
  openAddProductModal() {
    this.isEditMode = false;
    this.currentProduct = {
      nom: '',
      description: '',
      prix: 0,
      stock: 0,
    };
    this.errorMessage = '';
    this.successMessage = '';
    this.showModal = true;
  }

  editProduct(product: Product) {
    this.isEditMode = true;
    this.currentProduct = { ...product };
    this.errorMessage = '';
    this.successMessage = '';
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
    this.errorMessage = '';
    this.successMessage = '';
  }

  viewProduct(product: Product) {
    this.selectedProduct = product;
    this.showViewModal = true;
  }

  closeViewModal() {
    this.showViewModal = false;
    this.selectedProduct = null;
  }

  saveProduct() {
    // Réinitialiser les messages
    this.errorMessage = '';
    this.successMessage = '';

    // Validation
    if (!this.currentProduct.nom || !this.currentProduct.description) {
      this.errorMessage = 'Le nom et la description sont obligatoires';
      return;
    }

    if (this.currentProduct.prix <= 0) {
      this.errorMessage = 'Le prix doit être supérieur à 0';
      return;
    }

    if (this.currentProduct.stock < 0) {
      this.errorMessage = 'Le stock ne peut pas être négatif';
      return;
    }

    // Activer le chargement
    this.isLoading = true;

    if (this.isEditMode && this.currentProduct.id) {
      // Update
      this.productService.updateProduct(Number(this.currentProduct.id), this.currentProduct).subscribe({
        next: (response) => {
          this.isLoading = false;
          this.successMessage = response.message || 'Produit mis à jour avec succès !';
          this.loadProducts();

          // Fermer le modal après 1.5 secondes
          setTimeout(() => {
            this.closeModal();
          }, 1500);
        },
        error: (error) => {
          this.isLoading = false;

          if (error.status === 404) {
            this.errorMessage = 'Produit introuvable';
          } else if (error.status === 400) {
            this.errorMessage = error.error?.message || 'Données invalides';
          } else if (error.status === 0) {
            this.errorMessage = 'Impossible de contacter le serveur';
          } else {
            this.errorMessage = error.error?.message || 'Erreur lors de la mise à jour du produit';
          }
        }
      });
    } else {
      // Create
      this.productService.createProduct(this.currentProduct).subscribe({
        next: (response) => {
          this.isLoading = false;
          this.successMessage = response.message || 'Produit créé avec succès !';
          this.loadProducts();

          // Fermer le modal après 1.5 secondes
          setTimeout(() => {
            this.closeModal();
          }, 1500);
        },
        error: (error) => {
          this.isLoading = false;

          if (error.status === 400) {
            this.errorMessage = error.error?.message || 'Données invalides';
          } else if (error.status === 0) {
            this.errorMessage = 'Impossible de contacter le serveur';
          } else {
            this.errorMessage = error.error?.message || 'Erreur lors de la création du produit';
          }
        }
      });
    }
  }

  deleteProduct(product: Product) {
    if (!product.id) {
      this.errorMessage = 'Produit sans ID';
      return;
    }

    if (confirm(`Êtes-vous sûr de vouloir supprimer "${product.nom}" ?`)) {
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';

      this.productService.deleteProduct(Number(product.id)).subscribe({
        next: (response) => {
          this.isLoading = false;
          this.successMessage = response?.message || 'Produit supprimé avec succès !';
          this.loadProducts();

          // Effacer le message après 2 secondes
          setTimeout(() => {
            this.successMessage = '';
          }, 2000);
        },
        error: (error) => {
          this.isLoading = false;

          if (error.status === 404) {
            this.errorMessage = 'Produit introuvable';
          } else if (error.status === 0) {
            this.errorMessage = 'Impossible de contacter le serveur';
          } else {
            this.errorMessage = error.error?.message || 'Erreur lors de la suppression du produit';
          }
        }
      });
    }
  }

  logout() {
    this.authService.logout();
  }
}
