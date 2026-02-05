package com.example.gestionproduits.controller;

import com.example.gestionproduits.dto.ProduitReponse;
import com.example.gestionproduits.entity.Produits;
import com.example.gestionproduits.entity.User;
import com.example.gestionproduits.exception.InsufficientStokException;
import com.example.gestionproduits.exception.InvalidProduitExtension;
import com.example.gestionproduits.exception.ProduitNotFoundException;
import com.example.gestionproduits.response.ApiResponse;
import com.example.gestionproduits.service.ProduitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@CrossOrigin(origins = "*")
public class ProduitContoller {

    private final ProduitService produitService;

    public ProduitContoller(ProduitService produitService) {
        this.produitService = produitService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllProduits(@AuthenticationPrincipal User user){
        List<Produits> produits = produitService.getAllProduits(user);
        List<ProduitReponse> ProduitDto = produits.stream().map(ProduitReponse::fromEntity).toList();
        return ResponseEntity.ok(ApiResponse.success("Liste des produits recuperés avec succes", ProduitDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProduitById(@PathVariable Long id, @AuthenticationPrincipal User user){
        Produits produits = produitService.getProduitById(id, user);
        ProduitReponse dto = ProduitReponse.fromEntity(produits);
        return ResponseEntity.ok(ApiResponse.success("Produit trouvé avec succes", dto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createProduit(@RequestBody Produits produits, @AuthenticationPrincipal User user){
        produits.setUser(user);
        Produits newProduit = produitService.createProduit(produits);
        ProduitReponse dto = ProduitReponse.fromEntity(newProduit);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Produit crée avec succes",dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProduit(@PathVariable Long id, @RequestBody Produits produits, @AuthenticationPrincipal User user){
        Produits updateProduit = produitService.updatedProduit(id, produits, user);
        ProduitReponse dto = ProduitReponse.fromEntity(updateProduit);
        return ResponseEntity.ok(ApiResponse.success("Produit mis a jour", dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletProduit(@PathVariable Long id, @AuthenticationPrincipal User user){
        produitService.deleteProduit(id, user);
        return ResponseEntity.ok(ApiResponse.success("Produit supprimé avec succes", null));
    }

    @PatchMapping("/{id}/reduire-Stock")
    public ResponseEntity<ApiResponse> reduireStock(@PathVariable Long id, @RequestParam int quantité, @AuthenticationPrincipal User user ){
        Produits produits = produitService.reduireStock(id, quantité, user);
        ProduitReponse dto = ProduitReponse.fromEntity(produits);
        return ResponseEntity.ok(ApiResponse.success("Stock reduit de " + quantité + "unité(s) avec succes", dto));
    }

    @PatchMapping("/{id}/augmenter-Stock")
    public ResponseEntity<ApiResponse> augmenteStock(@PathVariable Long id, @RequestParam int quantité, @AuthenticationPrincipal User user){
        Produits produits = produitService.augmenteStock(id, quantité, user);
        ProduitReponse dto = ProduitReponse.fromEntity(produits);
        return ResponseEntity.ok(ApiResponse.success("Stok augmenté de " + quantité + "unité (s) avec succes",dto));
    }

    @GetMapping("/{search}/price")
    public ResponseEntity<ApiResponse> seachByPrice (@RequestParam double min, @RequestParam double max, @AuthenticationPrincipal User user){
        List<Produits> produits = produitService.getProduitByPriceRange(user,min, max);
        List<ProduitReponse> dto = produits.stream().map(ProduitReponse::fromEntity).toList();
        return ResponseEntity.ok(ApiResponse.success(produits.size() + "produit(s) trouvé (s) entre " + min + "et" + max, dto));
    }

    @GetMapping("/rupture-stock")
    public ResponseEntity<ApiResponse> ruptureStock(@AuthenticationPrincipal User user){
        List<Produits> produits = produitService.getProduitsRupture(user);
        List<ProduitReponse> dto = produits.stream().map(ProduitReponse::fromEntity).toList();
        return ResponseEntity.ok(ApiResponse.success(produits.size() + "produit(s) en rupture de stock", dto));
    }

    @GetMapping("/stock-faible")
    public ResponseEntity<ApiResponse> stockFable( @RequestParam(defaultValue = "5") int seuil, @AuthenticationPrincipal User user) {
        List<Produits> produits = produitService.LowStock(user,seuil);
        List<ProduitReponse> dto = produits.stream().map(ProduitReponse::fromEntity).toList();
        return ResponseEntity.ok(ApiResponse.success(dto.size() + "produit(s) avec un stock <= " + seuil, dto));
    }

    @ExceptionHandler(ProduitNotFoundException.class)
    public ResponseEntity<ApiResponse> handleNonFound( ProduitNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error(ex.getMessage())
        );
    }

    @ExceptionHandler(InvalidProduitExtension.class)
    public ResponseEntity<ApiResponse> handleInvalid(InvalidProduitExtension ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.error(ex.getMessage())
        );
    }

    @ExceptionHandler(InsufficientStokException.class)
    public ResponseEntity<ApiResponse> handleInsuffissant(InsufficientStokException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse.error(ex.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneralError(Exception ex){
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.error("Une erreur inattendue s'est produite" + ex.getMessage())
        );
    }
}

