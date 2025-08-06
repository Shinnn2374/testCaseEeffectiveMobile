package com.example.bankcards.service;

import com.example.bankcards.dto.AuthResponse;
import com.example.bankcards.dto.UserLoginRequest;
import com.example.bankcards.dto.UserRegisterRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered", HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(Role.USER) // По умолчанию роль USER
                .build();

        userRepository.save(user);

        String token = jwtUtils.generateToken(user);
        return buildAuthResponse(user, token);
    }

    public AuthResponse login(UserLoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );


        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        String token = jwtUtils.generateToken(user);
        return buildAuthResponse(user, token);
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .expiresIn(jwtUtils.getExpiration())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }
}