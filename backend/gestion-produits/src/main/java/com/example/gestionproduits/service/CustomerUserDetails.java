package com.example.gestionproduits.service;

import com.example.gestionproduits.repository.userRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerUserDetails implements UserDetailsService {
    private  final userRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        return userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("utilisateur non trouvé avec l'email;" + email));
    }
}
