package com.example.gestionproduits.service;

import com.example.gestionproduits.dto.AuthResponse;
import com.example.gestionproduits.dto.LoginRequest;
import com.example.gestionproduits.dto.RegisterRequest;
import com.example.gestionproduits.entity.User;
import com.example.gestionproduits.repository.userRepository;
import com.example.gestionproduits.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class userService {
    private final userRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request){
        if (userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Cet email est deja utilisé");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(
                token,
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                "Inscription réussie"
        );
    }


    public AuthResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Email incorrect"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Mot de passe incorrect");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(
                token,
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                "Connexion réussie"
        );
    }
}
