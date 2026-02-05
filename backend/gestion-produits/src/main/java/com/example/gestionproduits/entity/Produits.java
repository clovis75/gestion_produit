package com.example.gestionproduits.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produits {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nom;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Double prix;
    @Column(nullable = false)
    private Integer stock;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "user_Id")
    private User user;
    @PrePersist
    protected void onCreate(){
         createdAt = LocalDateTime.now();
    }
}
