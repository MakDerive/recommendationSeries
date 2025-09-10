package com.example.seriesrecommend.controller;

import com.example.seriesrecommend.entity.Series;
import com.example.seriesrecommend.entity.UserEntity;
import com.example.seriesrecommend.entity.UserSeriesRating;
import com.example.seriesrecommend.repository.SeriesRatingRepository;
import com.example.seriesrecommend.repository.UserRepository;
import com.example.seriesrecommend.service.SeriesService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class ContentController {

    SeriesService seriesService;
    UserRepository userRepository;
    SeriesRatingRepository seriesRatingRepository;


    @GetMapping("/")
    public String index(
            Model model
    ) {
        List<Series> allSeries = seriesService.getSeries();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            UserEntity user = (UserEntity) auth.getPrincipal();
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmail());
            List<UserSeriesRating> likedRatings = seriesRatingRepository
                    .findLikedSeriesByUser(user.getId());
            List<UserSeriesRating> dislikedRatings = seriesRatingRepository
                    .findDislikedSeriesByUser(user.getId());

            List<Series> likedSeries = likedRatings.stream()
                            .map(UserSeriesRating::getSeries)
                                    .collect(Collectors.toList());

            List<Series> dislikedSeries = dislikedRatings.stream()
                    .map(UserSeriesRating::getSeries)
                    .collect(Collectors.toList());

            List<Series> recommendedSeries = seriesService.recommendSeries(likedSeries,dislikedSeries, user.getId());
            HashMap<Long, String> seriesStatus = seriesService.findSeriesStatus(allSeries,user);
            model.addAttribute("seriesStatus", seriesStatus);
            model.addAttribute("recommendSeries",recommendedSeries);
        }

        model.addAttribute("allSeries",allSeries);
        return "index";
    }

    @GetMapping("/search")
    public String searchSeries(@RequestParam(required = false) String query, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            UserEntity user = (UserEntity) auth.getPrincipal();
            model.addAttribute("username", user.getUsername());
        }

        List<Series> searchResults;
        if (query == null || query.isBlank()) {
            searchResults = seriesService.getSeries();
        } else {
            searchResults = seriesService.searchSeries(query.trim());
        }

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            UserEntity user = (UserEntity) auth.getPrincipal();
            HashMap<Long, String> seriesStatus = seriesService.findSeriesStatus(searchResults,user);
            model.addAttribute("seriesStatus", seriesStatus);
        }

        model.addAttribute("searchResults", searchResults);
        model.addAttribute("query", query != null ? query : "");
        return "search";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

}