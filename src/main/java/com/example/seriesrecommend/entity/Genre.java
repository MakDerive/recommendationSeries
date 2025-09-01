package com.example.seriesrecommend.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="genre")
@Setter
@Getter
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name",  unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "genres")
    private List<Series> series;

}
