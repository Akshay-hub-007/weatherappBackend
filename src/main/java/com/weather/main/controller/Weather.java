package com.weather.main.controller;

import com.weather.main.entity.History;
import com.weather.main.entity.Users;
import com.weather.main.repository.HistoryRepository;
import com.weather.main.repository.UserDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Weather {

    private static final Logger logger = LoggerFactory.getLogger(Weather.class);

    @Autowired
    private HistoryRepository historyRepository;

    private UserDetailsRepository ur;
    @PostMapping("/save")
    public ResponseEntity<String> addToDB(@RequestBody History history) {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            System.out.println(history.getCity());
            if (principal instanceof Users) {
                Users user = (Users) principal;
                history.setUser(user);

                History h=historyRepository.save(history);
                System.out.println(user.getUsername());
                System.out.println(user.getPoints()+3);
                ur.incrementPointsByUsername(user.getUsername(), (long) Math.floor(Math.random() * 3));

                return ResponseEntity.ok("User history saved");
            } else {
                return new ResponseEntity<>("Unauthorized: Invalid user", HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            return new ResponseEntity<>("Error in saving to DB history", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<History>> getHistory() {
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<History> history = historyRepository.findByUser(user);
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteHistory(@PathVariable Long id)
    {
        System.out.println("deleting");
        try{
            historyRepository.deleteById(id);

           return  ResponseEntity.ok("deleted successfully");
        }catch(Exception e)
        {
            return new ResponseEntity<>("Error in deleting the history",HttpStatus.BAD_REQUEST);
        }
    }

}
