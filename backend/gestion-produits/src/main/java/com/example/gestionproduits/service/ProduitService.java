package com.example.gestionproduits.service;

import com.example.gestionproduits.entity.Produits;
import com.example.gestionproduits.entity.User;
import com.example.gestionproduits.exception.InvalidProduitExtension;
import com.example.gestionproduits.exception.ProduitNotFoundException;
import com.example.gestionproduits.repository.ProduitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProduitService {
    private final ProduitRepository produitRepository;
    public List<Produits> getAllProduits(User user){
        return produitRepository.findByUser(user);
    }
    public Produits getProduitById(Long id, User user){
        return produitRepository.findByIdAndUser(id, user).orElseThrow(() -> new ProduitNotFoundException(id));
    }

    @Transactional
    public Produits createProduit(Produits produits){
        if (produits.getNom() == null || produits.getNom().trim().isEmpty()){
            throw new InvalidProduitExtension("Le nom du produit est obligaoire");
        }
        if (produitRepository.existsByNom(produits.getNom().trim().toUpperCase())){
            throw new InvalidProduitExtension("Un produit existe deja avec ce nom");
        }
        if (produits.getPrix() == null || produits.getPrix()<=0){
            throw new InvalidProduitExtension("Le prix doit etre positif et superieure ou egale a 0");
        }
        if (produits.getStock() == null || produits.getStock() < 0){
            throw new InvalidProduitExtension("La quantité du produit doit etre strictement positive");
        }
        produits.setNom(produits.getNom().trim().toUpperCase());
        if (produits.getDescription() == null){
            produits.setDescription("");
        }
        return produitRepository.save(produits);
    }

    @Transactional
    public Produits updatedProduit(Long id, Produits produitsDetail, User user){
        Produits existingProduit = produitRepository.findByIdAndUser (id, user).orElseThrow(() -> new ProduitNotFoundException(id));
        if (produitsDetail.getNom() == null &&  produitsDetail.getNom().trim().isEmpty()){
            throw new InvalidProduitExtension("Le nom du produit est obligatoire");
        }
        if (produitsDetail.getPrix() == null || produitsDetail.getPrix() <= 0){
            throw new InvalidProduitExtension("Le prix du produit doit etre positif et superieur a 0");
        }
        if (produitsDetail.getStock() == null || produitsDetail.getStock() < 0){
            throw new InvalidProduitExtension("La quantité du produit doit etre positive");
        }
        existingProduit.setNom(produitsDetail.getNom().trim().toUpperCase());
        existingProduit.setDescription(produitsDetail.getDescription());
        existingProduit.setPrix(produitsDetail.getPrix());
        existingProduit.setStock(produitsDetail.getStock());
        return produitRepository.save(existingProduit);
    }

    @Transactional
    public void deleteProduit(Long id, User user){
        Produits produits = produitRepository.findByIdAndUser(id, user).orElseThrow(() -> new ProduitNotFoundException(id));
        produitRepository.delete(produits);
    }

    @Transactional
    public Produits reduireStock(Long id, int quantité, User user){
        Produits produits = getProduitById(id, user);
        if (quantité <= 0){
            throw new InvalidProduitExtension("La quantité a réduire doit etre positive");
        }
        if (produits.getStock() < quantité){
            throw new InvalidProduitExtension("Stock insuiffisant pour le produit'" + produits.getNom() + "'." + "Disponible: " + produits.getStock() + ", Demandé : " + quantité);
        }
        produits.setStock(produits.getStock() - quantité);
        return produitRepository.save(produits);
    }

    @Transactional
    public Produits augmenteStock(Long id, int quantité, User user){
        Produits produits = getProduitById(id, user);
        if (quantité <= 0){
            throw new InvalidProduitExtension("La quantité à ajouter doit etre positive");
        }
        produits.setStock(produits.getStock() + quantité);
        return produitRepository.save(produits);
    }

    public List<Produits> getProduitByPriceRange(User user, double minPrix, double maxPrix){
        if (minPrix < 0 || maxPrix < 0){
            throw new InvalidProduitExtension("Les prix ne peuvent pas etre negatifs");
        }
        if (minPrix > maxPrix){
            throw new InvalidProduitExtension("Le prix minimum (" + minPrix + ") ne peut pas etre superieure au prix maximal (" + maxPrix + ")");
        }
        return produitRepository.findByUserAndPrixBetween(user, minPrix, maxPrix);
    }

    public List<Produits> getProduitsRupture(User user){
        return produitRepository.findByUserAndStockLessThanEqual(user,0);
    }

    public List<Produits> LowStock(User user, int seuil) {
        if (seuil < 0){
            throw new InvalidProduitExtension("Le seuil ne peut pas etre negatif");
        }
        return produitRepository.findByUserAndStockLessThanEqual(user, seuil);
    }
}
