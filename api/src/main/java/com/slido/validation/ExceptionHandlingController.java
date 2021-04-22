package com.slido.validation;

import com.google.common.collect.Sets;
import com.slido.book.model.ApiError;
import cz.jirutka.rsql.parser.ParseException;
import cz.jirutka.rsql.parser.RSQLParserException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getSimpleName() + " " +
                    violation.getPropertyPath() + ": " + violation.getMessage());
        }

        ApiError apiError = new ApiError().code(HttpStatus.CONFLICT.toString()).message(ex.getMessage()).errors(errors);
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RollbackException.class)
    public ResponseEntity<ApiError> handleRollback(
            RollbackException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();
        Optional<ConstraintViolationException> rootCause = findException(ex, ConstraintViolationException.class);
        for (ConstraintViolation<?> violation : rootCause.map(ConstraintViolationException::getConstraintViolations).orElse(Sets.newHashSet())) {
            errors.add(violation.getRootBeanClass().getSimpleName() + " " +
                    violation.getPropertyPath() + ": " + violation.getMessage());
        }

        ApiError apiError = new ApiError().code(HttpStatus.CONFLICT.toString()).message(ex.getMessage()).errors(errors);
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RSQLParserException.class)
    public ResponseEntity<ApiError> handleRqslParsing(
            RSQLParserException ex, WebRequest request) {
        Optional<ParseException> rootCause = findException(ex, ParseException.class);
        ApiError apiError = new ApiError().code(HttpStatus.BAD_REQUEST.toString())
                .message(
                        rootCause.map($ -> $.currentToken + " -> " + $.getMessage())
                                .orElse(ExceptionUtils.getRootCauseMessage(ex))
                );
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    private final static <T> Optional<T> findException(Exception ex, Class<T> clazz) {
        return ExceptionUtils
                .getThrowableList(ex).stream()
                .filter(f -> f.getClass().isAssignableFrom(clazz))
                .map(f -> (T) f)
                .findFirst();
    }
}