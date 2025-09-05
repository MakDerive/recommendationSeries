package com.example.seriesrecommend.controller;

import com.example.seriesrecommend.entity.RatingStatus;
import com.example.seriesrecommend.entity.Series;
import com.example.seriesrecommend.entity.UserEntity;
import com.example.seriesrecommend.entity.UserSeriesRating;
import com.example.seriesrecommend.repository.SeriesRatingRepository;
import com.example.seriesrecommend.repository.SeriesRepository;
import com.example.seriesrecommend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/api/series")
@AllArgsConstructor

public class SeriesApiController {

    SeriesRepository seriesRepository;
    UserRepository userRepository;
    SeriesRatingRepository ratingRepository;


    @PostMapping("/{seriesId}/like")
    public ResponseEntity<Void> likeSeries(@PathVariable Long seriesId,
                                           @AuthenticationPrincipal UserEntity user) {
        Series series = seriesRepository.findById(seriesId).orElseThrow(
                ()->new RuntimeException("No such series")
        );
        Optional<UserSeriesRating> existingRating = ratingRepository
                .findByUserIdAndSeriesId(user.getId(), seriesId);

        UserSeriesRating rating;

        if(existingRating.isPresent()) {
            rating = existingRating.get();
            rating.setStatus(RatingStatus.LIKE);
        } else {
            rating = new UserSeriesRating(user,series,RatingStatus.LIKE);
        }
        ratingRepository.save(rating);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{seriesId}/dislike")
    public ResponseEntity<Void> dislikeSeries(@PathVariable Long seriesId,
                                           @AuthenticationPrincipal UserEntity user) {
        Series series = seriesRepository.findById(seriesId).orElseThrow(
                ()->new RuntimeException("No such series")
        );
        Optional<UserSeriesRating> existingRating = ratingRepository
                .findByUserIdAndSeriesId(user.getId(), seriesId);

        UserSeriesRating rating;

        if(existingRating.isPresent()) {
            rating = existingRating.get();
            rating.setStatus(RatingStatus.LIKE);
        } else {
            rating = new UserSeriesRating(user,series,RatingStatus.DISLIKE);
        }
        ratingRepository.save(rating);

        return ResponseEntity.ok().build();
    }



}
