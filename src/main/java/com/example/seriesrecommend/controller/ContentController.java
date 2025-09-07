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

import java.util.Collections;
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            UserEntity userDetails = (UserEntity) auth.getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
            model.addAttribute("email", userDetails.getEmail());
            List<UserSeriesRating> likedRatings = seriesRatingRepository
                    .findLikedSeriesByUser(userDetails.getId());

            List<Series> likedSeries = likedRatings.stream()
                            .map(UserSeriesRating::getSeries)
                                    .collect(Collectors.toList());


            model.addAttribute("likedSeries",likedSeries);
        }
        List<Series> allSeries = seriesService.getSeries();
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

        model.addAttribute("searchResults", searchResults);
        model.addAttribute("query", query != null ? query : "");
        return "search";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

}