package com.pmf.awp.project.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pmf.awp.project.dto.LoginDTO;
import com.pmf.awp.project.dto.RegisterDTO;
import com.pmf.awp.project.exception.GenericHttpException;
import com.pmf.awp.project.model.User;
import com.pmf.awp.project.service.AuthenticationService;
import com.pmf.awp.project.service.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterDTO registerDTO) {
        authenticationService.signup(registerDTO);
        return ResponseEntity.ok("Registration successed");
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        User authenticatedUser = authenticationService.authenticate(loginDTO);

        String jwtToken = jwtService.generateToken(authenticatedUser);
        String refreshToken = authenticationService.generateRefreshToken(authenticatedUser);
        ResponseCookie responseCookie = ResponseCookie.from("refresh-token", refreshToken)
                .secure(true)
                .httpOnly(true)
                .path("/auth").maxAge(86400)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() == null)
            throw new GenericHttpException("Token is already expired", HttpStatus.UNAUTHORIZED);
        String value = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("refresh-token"))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new GenericHttpException("Token is already expired", HttpStatus.UNAUTHORIZED));
        authenticationService.logout(value);
        ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                .secure(true)
                .httpOnly(true)
                .path("/auth")
                .maxAge(0)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        return ResponseEntity.ok("User now logged out.");
    }

    @GetMapping("/refresh")
    public ResponseEntity<String> refresh(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() == null)
            throw new GenericHttpException("Token is already expired", HttpStatus.UNAUTHORIZED);
        String value = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("refresh-token"))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new GenericHttpException("Token is already expired", HttpStatus.UNAUTHORIZED));
        User authenticatedUser = null;
        try {
            authenticatedUser = authenticationService.refresh(value);
        } catch (Exception e) {
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                    .secure(true)
                    .httpOnly(true)
                    .path("/auth")
                    .maxAge(0)
                    .sameSite("None")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
            throw e;
        }
        return ResponseEntity.ok(jwtService.generateToken(authenticatedUser));
    }
}
