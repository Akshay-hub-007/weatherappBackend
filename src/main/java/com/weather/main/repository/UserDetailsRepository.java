package com.weather.main.repository;

import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.weather.main.entity.Users;

@Repository
public interface UserDetailsRepository extends JpaRepository<Users,Long> {
    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.points = u.points + :increment WHERE u.username = :username")
    void incrementPointsByUsername(String username, Long increment);

}