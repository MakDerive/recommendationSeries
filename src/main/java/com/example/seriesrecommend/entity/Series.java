package com.example.seriesrecommend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

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


    @OneToMany(mappedBy = "series")
    private Set<SeriesGenre> genres = new HashSet<>();

    @Column(nullable = false, precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(nullable = false)
    private Integer releaseYear;

    @Column(nullable = false)
    private Integer updateYear;

    @Column(nullable = false)
    private Integer seasonsCount;

}
