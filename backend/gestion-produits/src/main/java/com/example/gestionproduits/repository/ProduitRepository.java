package com.example.gestionproduits.repository;

import com.example.gestionproduits.entity.Produits;
import com.example.gestionproduits.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produits, Long>  {
    boolean existsByNom(String nom);
    List<Produits> findByUserAndPrixBetween(User user, double minPrice, double maxPrice);
    List<Produits> findByUserAndStockLessThanEqual(User user, int stock);
    Optional<Produits> findByIdAndUser(Long id, User user);

    List<Produits> findByUser(User user);
}
