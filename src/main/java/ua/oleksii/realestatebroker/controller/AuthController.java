package ua.oleksii.realestatebroker.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ua.oleksii.realestatebroker.dto.LoginRequest;
import ua.oleksii.realestatebroker.dto.RegisterRequest;
import ua.oleksii.realestatebroker.model.User;
import ua.oleksii.realestatebroker.repository.UserRepository;
import ua.oleksii.realestatebroker.service.JwtBlacklistService;
import ua.oleksii.realestatebroker.service.JwtService;
import ua.oleksii.realestatebroker.service.UserService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtBlacklistService jwtBlacklistService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(403).body("Неправильний email або пароль");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(403).body("Неправильний email або пароль");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(Map.of("accessToken", accessToken, "role", user.getRole()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        System.out.println("Запит на реєстрацію отримано: " + request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(400).body("Ця електронна пошта вже використовується");
        }

        User newUser = new User();
        newUser.setFullName(request.getFullName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setPhone(request.getPhone());
        newUser.setRole(request.getRole());
        newUser.setAgency(request.getAgency());
        newUser.setTelegram(request.getTelegram());

        userRepository.save(newUser);

        return ResponseEntity.ok(Map.of("message", "Реєстрація успішна!"));
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            return ResponseEntity.status(401).body("Refresh token is missing");
        }

        Optional<User> userOptional = userRepository.findByRefreshToken(refreshToken);

        if (userOptional.isEmpty() || jwtBlacklistService.isTokenBlacklisted(refreshToken)) {
            return ResponseEntity.status(401).body("Недійсний або заблокований refresh token");
        }

        User user = userOptional.get();
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        Cookie newRefreshCookie = new Cookie("refreshToken", newRefreshToken);
        newRefreshCookie.setHttpOnly(true);
        newRefreshCookie.setPath("/");
        newRefreshCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(newRefreshCookie);

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response, @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            jwtBlacklistService.invalidateToken(token);
        }

        if (refreshToken != null) {
            Optional<User> userOptional = userRepository.findByRefreshToken(refreshToken);
            userOptional.ifPresent(user -> {
                user.setRefreshToken(null);
                userRepository.save(user);
            });

            jwtBlacklistService.invalidateToken(refreshToken);
        }

        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(Map.of("message", "Вихід виконано успішно"));
    }
}
