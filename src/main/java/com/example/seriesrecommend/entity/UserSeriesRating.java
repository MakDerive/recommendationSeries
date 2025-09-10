package com.example.seriesrecommend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_series_ratings")
@Getter
@Setter
@NoArgsConstructor
public class UserSeriesRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id", nullable = false)
    private Series series;

    @Enumerated(EnumType.STRING)
    private RatingStatus status;

    public UserSeriesRating(UserEntity user, Series series, RatingStatus status) {
        this.user = user;
        this.series = series;
        this.status = status;
    }
}
