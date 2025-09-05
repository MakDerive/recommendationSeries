package com.example.seriesrecommend.service;

import com.example.seriesrecommend.entity.Token;
import com.example.seriesrecommend.entity.UserEntity;
import com.example.seriesrecommend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService  tokenService;
    private final EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public void registerUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(false);
        userRepository.save(user);

        Token confirmToken = new Token(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );

        tokenService.save(confirmToken);
        emailService.sendConfirm(user.getEmail(),confirmToken.getToken());

    }

    public void confirmToken(String token) {
        Token confirmedToken = tokenService.findByToken(token).orElseThrow(
                () -> new RuntimeException("Token not found"));

        if(confirmedToken.getConfirmedAt() != null){
            throw new RuntimeException("Token already confirmed");
        }

        LocalDateTime expiresAt = confirmedToken.getExpiresAt();
        if(expiresAt.isBefore(LocalDateTime.now())){
            throw new RuntimeException("Token expired");
        }

        confirmedToken.setConfirmedAt(LocalDateTime.now());
        tokenService.save(confirmedToken);

        enableUser(confirmedToken.getUser());
    }

    public void enableUser(UserEntity user) {
        user.setEnabled(true);
        System.out.println(user.getEnabled());
        userRepository.save(user);
    }

    public void resetPassword(String email) {

        UserEntity user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("No user with email: " + email)
        );
        Token resetToken = new Token(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );
        tokenService.save(resetToken);
        emailService.sendResetPassword(user.getEmail(),resetToken.getToken());

    }

    public void newPassword(UserEntity user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }



    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


}
