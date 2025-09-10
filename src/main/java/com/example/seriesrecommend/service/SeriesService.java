package com.example.seriesrecommend.service;

import com.example.seriesrecommend.entity.*;
import com.example.seriesrecommend.repository.SeriesGenreRepository;
import com.example.seriesrecommend.repository.SeriesRatingRepository;
import com.example.seriesrecommend.repository.SeriesRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SeriesService {

    SeriesRepository seriesRepository;
    SeriesRatingRepository seriesRatingRepository;
    SeriesGenreRepository seriesGenreRepository;

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

    public List<Series> recommendSeries(List<Series> likedSeries, List<Series> dislikedSeries, Long userId) {
        if (likedSeries.isEmpty() && dislikedSeries.isEmpty()) {
            return getPopularSeries(10);
        }

        Set<Long> ratedSeriesIds = getRatedSeriesIds(userId);


        Map<Genre, Double> genreWeights = calculateGenreWeights(likedSeries, dislikedSeries);

        List<Series> allSeries = seriesRepository.findAll();
        List<SeriesCandidate> candidates = new ArrayList<>();

        for (Series series : allSeries) {
            if (!ratedSeriesIds.contains(series.getId())) {
                double score = calculateSeriesScore(series, genreWeights, likedSeries);
                candidates.add(new SeriesCandidate(series, score));
            }
        }

        return candidates.stream()
                .sorted((c1, c2) -> Double.compare(c2.score, c1.score))
                .map(candidate -> candidate.series)
                .limit(10)
                .collect(Collectors.toList());
    }

    private Map<Genre, Double> calculateGenreWeights(List<Series> likedSeries, List<Series> dislikedSeries) {
        Map<Genre, Double> genreWeights = new HashMap<>();

        final double LIKE_WEIGHT = 2.0;
        final double DISLIKE_WEIGHT = -3.0;

        for (Series series : likedSeries) {
            List<SeriesGenre> seriesGenres = seriesGenreRepository.findBySeriesId(series.getId());
            for (SeriesGenre seriesGenre : seriesGenres) {
                Genre genre = seriesGenre.getGenre();
                genreWeights.put(genre, genreWeights.getOrDefault(genre, 0.0) + LIKE_WEIGHT);
            }
        }

        for (Series series : dislikedSeries) {
            List<SeriesGenre> seriesGenres = seriesGenreRepository.findBySeriesId(series.getId());
            for (SeriesGenre seriesGenre : seriesGenres) {
                Genre genre = seriesGenre.getGenre();
                genreWeights.put(genre, genreWeights.getOrDefault(genre, 0.0) + DISLIKE_WEIGHT);
            }
        }

        return genreWeights;
    }

    private double calculateSeriesScore(Series series, Map<Genre, Double> genreWeights, List<Series> likedSeries) {
        double score = 0.0;

        double ratingScore = series.getRating().doubleValue() * 0.2;

        List<SeriesGenre> seriesGenres = seriesGenreRepository.findBySeriesId(series.getId());
        double genreScore = 0.0;
        int genreCount = 0;

        for (SeriesGenre seriesGenre : seriesGenres) {
            Genre genre = seriesGenre.getGenre();
            double genreWeight = genreWeights.getOrDefault(genre, 0.0);
            genreScore += genreWeight;
            genreCount++;
        }

        if (genreCount > 0) {
            genreScore /= genreCount;
        }

        double yearBonus = calculateYearBonus(series.getUpdateYear());

        double similarityPenalty = calculateSimilarityPenalty(series, likedSeries);

        score = (ratingScore * 0.3) + (genreScore * 0.5) + (yearBonus * 0.1) + (similarityPenalty * 0.1);

        return score;
    }

    private double calculateYearBonus(Integer updateYear) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int yearDifference = currentYear - updateYear;

        return Math.max(0, 1.0 - (yearDifference * 0.1));
    }

    private double calculateSimilarityPenalty(Series series, List<Series> likedSeries) {
        if (likedSeries.isEmpty()) return 0.0;
        int avgSeasons = (int) likedSeries.stream()
                .mapToInt(Series::getSeasonsCount)
                .average()
                .orElse(0);

        int seasonsDiff = Math.abs(series.getSeasonsCount() - avgSeasons);

        return -Math.min(1.0, seasonsDiff * 0.1);
    }

    private Set<Long> getRatedSeriesIds(Long userId) {
        return seriesRatingRepository.findByUserId(userId).stream()
                .map(rating -> rating.getSeries().getId())
                .collect(Collectors.toSet());
    }

    private List<Series> getPopularSeries(int limit) {
        return seriesRepository.findAll().stream()
                .sorted((s1, s2) -> Double.compare(s2.getRating().doubleValue(), s1.getRating().doubleValue()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private static class SeriesCandidate {
        Series series;
        double score;

        SeriesCandidate(Series series, double score) {
            this.series = series;
            this.score = score;
        }
    }
}
