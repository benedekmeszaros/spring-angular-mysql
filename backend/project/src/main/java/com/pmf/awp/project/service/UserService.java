package com.pmf.awp.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.pmf.awp.project.exception.GenericHttpException;
import com.pmf.awp.project.model.User;
import com.pmf.awp.project.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new GenericHttpException("User not found", HttpStatus.NOT_FOUND));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new GenericHttpException("User not found", HttpStatus.NOT_FOUND));
    }

    public User save(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new GenericHttpException("Unable to update user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
