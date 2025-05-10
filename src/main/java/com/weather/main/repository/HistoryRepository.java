package com.weather.main.repository;

import com.weather.main.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weather.main.entity.History;

import java.util.List;

@Repository
public interface HistoryRepository  extends JpaRepository<History,Long>{
    List<History> findByUser(Users user);
}
