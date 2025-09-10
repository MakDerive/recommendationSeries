package com.example.seriesrecommend.repository;

import com.example.seriesrecommend.entity.SeriesGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeriesGenreRepository extends JpaRepository<SeriesGenre,Long> {


    List<SeriesGenre> findBySeriesId(Long id);
}
