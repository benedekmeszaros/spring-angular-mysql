package com.pmf.awp.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GenericHttpException.class)
    public ResponseEntity<String> handleGenericHttpException(GenericHttpException exception) {
        return new ResponseEntity<>(exception.getMessage(), exception.getStatus());
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<String> handleSignatureException(SignatureException exception) {
        return new ResponseEntity<>("The JWT signature is invalid", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException exception) {
        return new ResponseEntity<>("The username or password is incorrect", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<String> handleMalformedJwtException(Exception exception) {
        return new ResponseEntity<>("The provided token is invalid", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(Exception exception) {
        return new ResponseEntity<>("The account is locked", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> handleExpiredJwtException(Exception exception) {
        return new ResponseEntity<>("The JWT token has expired", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(Exception exception) {
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        String description;
        FieldError fieldError = exception.getFieldError();
        if (fieldError != null)
            description = fieldError.getDefaultMessage();
        else
            description = "Invalid argument(s)";
        return new ResponseEntity<>(description, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<String> handleDisabledException(Exception exception) {
        return new ResponseEntity<>("User suspended", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleSecurityException(Exception exception) {
        exception.printStackTrace();
        return new ResponseEntity<>("Unknown internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
