package com.example.seriesrecommend.controller;

import com.example.seriesrecommend.dto.RegistrationRequest;
import com.example.seriesrecommend.entity.Token;
import com.example.seriesrecommend.entity.UserEntity;
import com.example.seriesrecommend.repository.TokenRepository;
import com.example.seriesrecommend.repository.UserRepository;
import com.example.seriesrecommend.service.TokenService;
import com.example.seriesrecommend.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Slf4j
@Controller
@AllArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private  UserService userService;


    @GetMapping("registration")
    public String registration(Model model) {
        model.addAttribute("registrationRequest",new RegistrationRequest());
        return "registration";
    }

    @PostMapping("registration")
    public String registerUser(@Valid @ModelAttribute("registrationRequest") RegistrationRequest registrationRequest,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        try {
            userService.registerUser(registrationRequest);
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Please confirm your email address");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "registration";
        }
    }

    @GetMapping("/registration/confirm_token")
    public String confirmToken(@RequestParam("token") String token, Model model) {
        userService.confirmToken(token);
        return "confirm_token";
    }

    @GetMapping("/reset_password")
    public String resetPassword() {
        return "reset_password";
    }

    @PostMapping("/reset_password")
    public String resetPassword(@RequestParam String email,
                                Model model) {
        try {
            userRepository.findByEmail(email).orElseThrow(
                    () -> new RuntimeException("Email not found")
            );
            userService.resetPassword(email);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "reset_password";
        }
        return "redirect:/login";
    }

    @GetMapping("/reset_password/new")
    public String newPassword(@RequestParam("token") String token,
                              Model model) {
        UserEntity user = tokenService.findByToken(token).orElseThrow(
                () -> new RuntimeException("Token not found")
        ).getUser();
        return "new_password";
    }

    @PostMapping("/reset_password/new")
    public String newPassword(@RequestParam("token") String token,
                              @RequestParam String password,
                              @RequestParam String confirmPassword,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "Пароли должны совпадать");
                model.addAttribute("token", token);
                return "new_password";
            }
            if (password.length() < 6) {
                model.addAttribute("error", "Пароль должен содержать минимум 6 символов");
                model.addAttribute("token", token);
                return "new_password";
            }

            Token resetToken = tokenService.findByToken(token).orElseThrow(
                    () -> new RuntimeException("Token not found")
            );

            if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Token expired");
            }

            UserEntity user = resetToken.getUser();
            userService.newPassword(user, password);
            userRepository.save(user);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Your password was reset successfully");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", token);
            return "new_password";
        }
    }

}
