package com.pmf.awp.project.service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pmf.awp.project.dto.LoginDTO;
import com.pmf.awp.project.dto.RegisterDTO;
import com.pmf.awp.project.exception.GenericHttpException;
import com.pmf.awp.project.model.RefreshToken;
import com.pmf.awp.project.model.User;
import com.pmf.awp.project.repository.RefreshTokenRepository;
import com.pmf.awp.project.repository.UserRepository;

@Service
public class AuthenticationService {
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private RefreshTokenRepository refreshTokenRepository;
        @Autowired
        private PasswordEncoder passwordEncoder;
        @Autowired
        private AuthenticationManager authenticationManager;

        public static User getAuthenticatedUser() {
                try {
                        return (User) SecurityContextHolder
                                        .getContext()
                                        .getAuthentication()
                                        .getPrincipal();
                } catch (Exception e) {
                        throw new GenericHttpException("Unauthorized", HttpStatus.UNAUTHORIZED);
                }
        }

        public User signup(RegisterDTO input) {
                if (userRepository.existsByEmail(input.getEmail()))
                        throw new GenericHttpException("Email address '" + input.getEmail() + "' is already taken",
                                        HttpStatus.BAD_REQUEST);
                User user = User.builder()
                                .enabled(true)
                                .fullName(input.getFullName())
                                .email(input.getEmail())
                                .password(passwordEncoder.encode(input.getPassword()))
                                .createdAt(new Date())
                                .updatedAt(new Date())
                                .build();

                return userRepository.save(user);
        }

        public User authenticate(LoginDTO input) {
                User user = userRepository.findByEmail(input.getEmail())
                                .orElseThrow(() -> new GenericHttpException("User not found", HttpStatus.NOT_FOUND));
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                input.getEmail(),
                                                input.getPassword()));
                return user;
        }

        public User refresh(String token) {
                RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                                .orElseThrow(() -> new GenericHttpException("Token not found", HttpStatus.NOT_FOUND));
                if (refreshToken.getExpireAt().compareTo(new Date()) < 0) {
                        refreshTokenRepository.delete(refreshToken);
                        throw new GenericHttpException("Refresh token exprired", HttpStatus.UNAUTHORIZED);
                } else
                        return refreshToken.getOwner();
        }

        public void logout(String token) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                User currentUser = (User) authentication.getPrincipal();
                RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                                .orElseThrow();
                if (currentUser.getId() == refreshToken.getOwner().getId())
                        refreshTokenRepository.delete(refreshToken);
                else
                        throw new GenericHttpException("Refresh token not matching with the credentials",
                                        HttpStatus.BAD_REQUEST);
        }

        public String generateRefreshToken(User user) {
                SecureRandom random = new SecureRandom();
                byte[] tokenBytes = new byte[16];
                random.nextBytes(tokenBytes);
                String token = user.getId() + Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes)
                                + Base64.getEncoder().encodeToString(new Date().toString().getBytes());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, 7);
                RefreshToken refreshToken = RefreshToken.builder()
                                .token(token)
                                .expireAt(calendar.getTime())
                                .owner(user)
                                .build();
                refreshTokenRepository.save(refreshToken);
                return token;
        }

        public User suspendAccount(User user) {
                try {
                        user.setEnabled(false);
                        return userRepository.save(user);
                } catch (Exception e) {
                        throw new GenericHttpException("Unable to suspend user", HttpStatus.INTERNAL_SERVER_ERROR);
                }
        }
}
