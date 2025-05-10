package com.weather.main.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String city;

    @Column(name = "weather_condition")
    private String condition;

    private int temperature;

    private LocalDateTime searchedAt;

    public History() {
    }
    public History(Users user, int temperature, String condition, String city) {
        this.user = user;
        this.temperature = temperature;
        this.condition = condition;
        this.city = city;
    }

    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    @JsonIgnore
    private Users user;

    @PrePersist
    public void onCreate() {
        this.searchedAt = LocalDateTime.now();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }


    public void setCity(String city) {
        this.city = city;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public LocalDateTime getSearchedAt() {
        return searchedAt;
    }

    public void setSearchedAt(LocalDateTime searchedAt) {
        this.searchedAt = searchedAt;
    }

    public Users getUser() {
        return user;
    }


    public void setUser(Users user) {
        this.user = user;
    }
}

