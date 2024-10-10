package com.diplom.cloudstorage.controllers;


import com.diplom.cloudstorage.dto.ErrorResponse;
import com.diplom.cloudstorage.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
        logger.error("BadCredentialsException: " + ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), 400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidInputDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInputDataException(InvalidInputDataException ex) {
        logger.error("InvalidInputDataException: " + ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), 400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        logger.error("UnauthorizedException: " + ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("UnauthorizedException: ", 401);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(FileDeletionException.class)
    public ResponseEntity<ErrorResponse> handleFileDeletionException(FileDeletionException ex) {
        logger.error("FileDeletionException: " + ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), 500);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ErrorResponse> handleFileUploadException(FileUploadException ex) {
        logger.error("FileUploadException: " + ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), 500);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(GettingFileListException.class)
    public ResponseEntity<ErrorResponse> handleGettingFileListException(GettingFileListException ex) {
        logger.error("GettingFileListException: " + ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), 500);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
