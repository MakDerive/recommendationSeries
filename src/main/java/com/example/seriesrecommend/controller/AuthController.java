package com.example.seriesrecommend.controller;

import com.example.seriesrecommend.entity.Token;
import com.example.seriesrecommend.entity.UserEntity;
import com.example.seriesrecommend.repository.UserRepository;
import com.example.seriesrecommend.service.TokenService;
import com.example.seriesrecommend.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Slf4j
@Controller
@AllArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private  UserService userService;
    private final Logger logger = Logger.getLogger(AuthController.class.getName());


    @GetMapping("registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("registration")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(username, email, password);
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
    public String resetPassword(@RequestParam String email,Model model) {
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
        logger.info(user.getEmail());
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
                throw new RuntimeException("Passwords do not match");
            }

            // Проверяем валидность токена
            Token resetToken = tokenService.findByToken(token).orElseThrow(
                    () -> new RuntimeException("Token not found")
            );

            // Проверяем не истек ли токен
            if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Token expired");
            }

            UserEntity user = resetToken.getUser();
            userService.newPassword(user, password);
            userRepository.save(user); // Не забудьте сохранить пользователя!

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Your password was reset successfully");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", token); // Возвращаем токен для повторной попытки
            return "new_password";
        }
    }

}
