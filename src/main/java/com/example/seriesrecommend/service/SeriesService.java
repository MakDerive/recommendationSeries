package com.example.seriesrecommend.service;

import com.example.seriesrecommend.entity.Series;
import com.example.seriesrecommend.repository.SeriesRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SeriesService {

    SeriesRepository seriesRepository;

    public List<Series> getSeries() {
        List<Series> series = seriesRepository.findAll();
        return series;
    }

    public List<Series> searchSeries(String query) {
        return seriesRepository.findByNameContainingIgnoreCase(query);
    }

    public  Optional<Series> findByName(String name){
        return seriesRepository.findByName(name);
    }

}
