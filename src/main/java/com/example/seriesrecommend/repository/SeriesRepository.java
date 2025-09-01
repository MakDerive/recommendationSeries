package com.example.seriesrecommend.repository;

import com.example.seriesrecommend.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeriesRepository extends JpaRepository<Series,Long> {
    Optional<Series> findByName(String name);
    List<Series> findAll();
    List<Series> findByNameContainingIgnoreCase(String name);
}
