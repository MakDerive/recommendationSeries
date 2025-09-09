package com.example.seriesrecommend.service;

import com.example.seriesrecommend.entity.RatingStatus;
import com.example.seriesrecommend.entity.Series;
import com.example.seriesrecommend.entity.UserEntity;
import com.example.seriesrecommend.repository.SeriesRatingRepository;
import com.example.seriesrecommend.repository.SeriesRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SeriesService {

    SeriesRepository seriesRepository;
    SeriesRatingRepository seriesRatingRepository;

    public List<Series> getSeries() {
        return seriesRepository.findAll();
    }



    public List<Series> searchSeries(String query) {
        return seriesRepository.findByNameContainingIgnoreCase(query);
    }

    public  Optional<Series> findByName(String name){
        return seriesRepository.findByName(name);
    }

    public HashMap<Long, String> findSeriesStatus(List<Series> seriesList, UserEntity user) {
        HashMap<Long, String> seriesStatus = new HashMap<>();
        for (Series series : seriesList) {
            seriesRatingRepository.findByUserIdAndSeriesId(user.getId(), series.getId())
                    .ifPresent(userSeriesRating -> seriesStatus.put(series.getId(), userSeriesRating.getStatus().toString()));
        }
        return  seriesStatus;
    }

}
