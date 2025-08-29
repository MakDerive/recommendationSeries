package com.example.seriesrecommend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "series")
@Setter
@Getter
public class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "series_img_url", unique = true, nullable = false, length = 512)
    private String seriesImgUrl;


//    @ManyToMany
//    @JoinTable(
//            name = "series_genres",
//            joinColumns = @JoinColumn(name = "series_id"),
//            inverseJoinColumns = @JoinColumn(name = "genre_id")
//    )
    //private List<Genre> genres;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(nullable = false)
    private LocalDate releaseDate;

    @Column(nullable = false)
    private LocalDate updateDate;

    @Column(nullable = false)
    private Integer seasonsCount;

}
