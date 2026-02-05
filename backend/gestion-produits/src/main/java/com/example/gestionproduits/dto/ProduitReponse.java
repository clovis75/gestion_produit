package com.example.gestionproduits.dto;

import com.example.gestionproduits.entity.Produits;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProduitReponse {
    private Long id;
    private String nom;
    private String description;
    private Double prix;
    private Integer stock;
    private LocalDateTime createdAt;
    private UserResponse user;

    public static ProduitReponse fromEntity(Produits produit){
        if (produit == null) return null;
        return new ProduitReponse(
                produit.getId(),
                produit.getNom(),
                produit.getDescription(),
                produit.getPrix(),
                produit.getStock(),
                produit.getCreatedAt(),
                new UserResponse(
                        produit.getUser().getUserId(),
                        produit.getUser().getFirstname(),
                        produit.getUser().getLastname(),
                        produit.getUser().getEmail()
                )
        );
    }
}
