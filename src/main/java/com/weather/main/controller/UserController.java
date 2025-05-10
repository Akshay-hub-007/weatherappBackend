package com.weather.main.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.weather.main.entity.History;
import com.weather.main.repository.HistoryRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.weather.main.entity.Users;
import com.weather.main.repository.UserDetailsRepository;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserController {

    @Autowired
  UserDetailsRepository userDetailsRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private HistoryRepository historyRepository;

    @GetMapping("/hello")
    public String hello() {
        System.out.println("hello");
        System.out.println(userDetailsRepository.findByUsername("akshay"));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("1234");
        System.out.println("Encoded password: " + encodedPassword);

        return "Hello from secured endpoint!";
    }

//    @PostMapping("/register")
//    public ResponseEntity<String> register(@RequestBody Users u) {
//        try {
//            System.out.println(u.getMobile());
//            System.out.println("register");
//            // System.out.println(userDetailsRepository.findByUsername(u.getUsername()));
//            Optional<Users> user=userDetailsRepository.findByEmail(u.getEmail());
//               System.out.println(user.isPresent());
//               if(user.isPresent())
//               {
//                  return new ResponseEntity<>("User with same mail already exists.",HttpStatus.NOT_FOUND);
//               }
//
//                u.setPassword(bCryptPasswordEncoder.encode(u.getPassword()));
//               Users newUser=userDetailsRepository.save(u);
//
//               return new ResponseEntity<>("User Registerd sucessfully",HttpStatus.OK);
//
//
//        } catch (Exception e) {
//            return new ResponseEntity<>("Error in registering the User",HttpStatus.NOT_FOUND);
//        }
//    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> register(
            @RequestPart("user") Users u,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        try {
            Optional<Users> user = userDetailsRepository.findByEmail(u.getEmail());
            if (user.isPresent()) {
                return new ResponseEntity<>("User with same mail already exists.", HttpStatus.NOT_FOUND);
            }

            if (imageFile != null && !imageFile.isEmpty()) {
                u.setProfileImage(imageFile.getBytes());
            }

            u.setPassword(bCryptPasswordEncoder.encode(u.getPassword()));
            System.out.println(u.getProfileImage());
            userDetailsRepository.save(u);
            return new ResponseEntity<>("User Registered successfully", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Error in registering the User", HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/getDetails")
    public Map<String, Object> getDetails() {
        Map<String, Object> userDetails = new HashMap<>();
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof Users) {
                Users user = (Users) principal;
                userDetails.put("username", user.getUsername());
                userDetails.put("email", user.getEmail());
                userDetails.put("points", user.getPoints());
                userDetails.put("image",user.getProfileImage());
                userDetails.put("mobile",user.getMobile());
            }
        } catch (Exception e) {
            System.out.println("Error in fetching User Details: " + e.getMessage());
            userDetails.put("error", "Failed to fetch user details");
        }
        return userDetails;
    }
    @GetMapping("/isLogged")
    public ResponseEntity<String> isLogged() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {

            Object principal = authentication.getPrincipal();

            // Optionally log or return user info
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                System.out.println("Logged in user: " + userDetails.getUsername());
            } else {
                System.out.println("Logged in principal: " + principal.toString());
            }

            return ResponseEntity.ok("User is logged in");
        } else {
            return ResponseEntity.status(401).body("User is not logged in");
        }
    }

    @GetMapping("/logoout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        System.out.println("logout");
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // set to false if not using HTTPS locally
        cookie.setPath("/"); // make sure it matches the original path
        cookie.setMaxAge(0); // expire immediately

        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out and JWT cookie cleared.");
    }

}