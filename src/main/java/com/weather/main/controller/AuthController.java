package com.weather.main.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.weather.main.entity.AuthRequest;
import com.weather.main.repository.UserDetailsRepository;
import com.weather.main.util.JWTUtil;

import org.springframework.http.HttpHeaders;

@RestController
public class AuthController {

    @Autowired
    UserDetailsRepository userDetailsRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JWTUtil jwtUtil;

    @PostMapping("/authenticate")
    public ResponseEntity<String> generateToken(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

            String token = jwtUtil.generateToken(authRequest.getUsername());
            System.out.println(token);
            System.out.println(token + "  generated");
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true); // Prevents JavaScript access (protects against XSS)
            cookie.setSecure(true); // Ensures cookie is sent only over HTTPS
            cookie.setPath("/"); // Send cookie with all requests to your domain
            cookie.setMaxAge(24 * 60 * 60);

            response.addCookie(cookie);

            return ResponseEntity.ok("Token set in cookie successfully");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Authentication failed");
        }
    }

}
