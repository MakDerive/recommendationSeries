package com.example.seriesrecommend.repository;

import com.example.seriesrecommend.entity.UserSeriesRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeriesRatingRepository extends JpaRepository<UserSeriesRating,Long> {

    Optional<UserSeriesRating> findByUserIdAndSeriesId(Long userId, Long seriesId);

    List<UserSeriesRating> findByUserId(Long userId);

    List<UserSeriesRating> findBySeriesId(Long seriesId);

    boolean existsByUserIdAndSeriesId(Long userId, Long seriesId);

}
