package com.pmf.awp.project.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pmf.awp.project.exception.GenericHttpException;
import com.pmf.awp.project.model.User;
import com.pmf.awp.project.service.AuthenticationService;
import com.pmf.awp.project.service.ImageService;
import com.pmf.awp.project.service.UserService;
import com.pmf.awp.project.util.ExporterUtils;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe() {
        return ResponseEntity.ok(ExporterUtils.extract(AuthenticationService.getAuthenticatedUser()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable("userId") Integer userId) {
        return ResponseEntity.ok(ExporterUtils.extract(userService.findById(userId)));
    }

    @PatchMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateAvatar(@RequestPart MultipartFile file) {
        User user = AuthenticationService.getAuthenticatedUser();
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1)
                .toLowerCase();
        String[] allowed = { "png", "jpg", "jpeg" };
        if (!Arrays.stream(allowed).anyMatch(extension::equals))
            throw new GenericHttpException("Disalowed file extension", HttpStatus.BAD_REQUEST);

        if (user.getAvatar() != null)
            imageService.delete(user.getAvatar());
        String url = imageService.save(file, user.getId() + "-avatar." + extension);
        user.setAvatar(url);
        userService.save(user);
        return ResponseEntity.ok(url);
    }

    @PatchMapping("/username")
    public ResponseEntity<Map<String, Object>> updateUsername(@RequestBody Map<String, Object> data) {
        User user = AuthenticationService.getAuthenticatedUser();
        String username = (String) data.get("username");
        if (username == null)
            throw new GenericHttpException("Invalid username", HttpStatus.BAD_REQUEST);
        user.setFullName(username);
        user = userService.save(user);
        return ResponseEntity.ok(ExporterUtils.extract(user));
    }

    @PatchMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody Map<String, Object> data) {
        User user = AuthenticationService.getAuthenticatedUser();
        String password = (String) data.get("password");
        if (password == null)
            throw new GenericHttpException("Invalid password", HttpStatus.BAD_REQUEST);
        String encoded = passwordEncoder.encode(password);
        if (encoded.equals(user.getPassword()))
            throw new GenericHttpException("Your old password can not be your new password", HttpStatus.BAD_REQUEST);
        user.setPassword(encoded);
        userService.save(user);
        return ResponseEntity.ok("Password changed");
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> suspend() {
        User user = AuthenticationService.getAuthenticatedUser();
        authenticationService.suspendAccount(user);
        return ResponseEntity.ok("Account suspended");
    }
}
