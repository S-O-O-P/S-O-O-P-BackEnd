package com.soop.jwtsecurity.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Void> handleNotFound(HttpServletRequest request, NoSuchElementException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/error/404");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Void> handleNoHandlerFound(HttpServletRequest request, Exception ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/error/404");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Void> handleInternalServerError(HttpServletRequest request, Exception ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/error/500");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Void> handleBadRequest(HttpServletRequest request, IllegalArgumentException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/error/400");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Void> handleAccessDenied(HttpServletRequest request, ForbiddenException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/error/403");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
