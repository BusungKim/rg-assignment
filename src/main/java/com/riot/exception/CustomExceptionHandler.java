package com.riot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({InvalidArgumentException.class, NoDataException.class})
    public ResponseEntity<Object> handleCustomException(final Exception exception, final WebRequest webRequest) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (exception instanceof InvalidArgumentException) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (exception instanceof NoDataException) {
            httpStatus = HttpStatus.NOT_FOUND;
        }

        final Map<String, Object> body = Map.of("status", httpStatus.value(),
                "message", exception.getMessage());

        return new ResponseEntity<>(body, httpStatus);
    }
}
