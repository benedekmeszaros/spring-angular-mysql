package com.pmf.awp.project.exception;

import org.springframework.http.HttpStatus;

public class GenericHttpException extends RuntimeException {
    private final HttpStatus status;

    public GenericHttpException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
