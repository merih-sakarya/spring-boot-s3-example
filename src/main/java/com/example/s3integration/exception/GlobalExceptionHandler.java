package com.example.s3integration.exception;

import com.example.s3integration.exception.constants.GlobalErrorConstants;
import com.example.s3integration.model.core.exception.ApiError;
import com.example.s3integration.model.core.exception.ErrorDetails;
import com.example.s3integration.model.core.exception.ErrorType;
import com.example.s3integration.model.core.exception.FieldError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle validation errors from request payload binding.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        logger.warn("Validation error", ex);
        List<FieldError> fieldErrors = getErrors(ex);
        ErrorDetails errorDetails = new ErrorDetails(ErrorType.VALIDATION, fieldErrors);
        return new ApiError(GlobalErrorConstants.ERR001.code(), GlobalErrorConstants.ERR001.message(), errorDetails);
    }

    /**
     * Handle unsupported HTTP methods.
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ApiError handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        logger.error("Method not allowed", ex);
        return new ApiError(GlobalErrorConstants.ERR004.code(), GlobalErrorConstants.ERR004.message(), null);
    }

    /**
     * Handle unsupported media types.
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    public ApiError handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        logger.error("Unsupported media type", ex);
        return new ApiError(GlobalErrorConstants.ERR005.code(), GlobalErrorConstants.ERR005.message(), null);
    }

    /**
     * Handle unexpected errors.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiError handleAllOtherExceptions(Exception ex) {
        logger.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return new ApiError(GlobalErrorConstants.ERR999.code(), GlobalErrorConstants.ERR999.message(), null);
    }

    /**
     * Transforms binding errors from a MethodArgumentNotValidException into a list of FieldErrors.
     *
     * @param ex the exception containing binding result errors
     * @return a list of FieldErrors
     */
    private List<FieldError> getErrors(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getAllErrors().stream()
                .filter(e -> e instanceof org.springframework.validation.FieldError)
                .map(e -> {
                    org.springframework.validation.FieldError fe = (org.springframework.validation.FieldError) e;
                    return new FieldError(fe.getField(), fe.getDefaultMessage());
                })
                .collect(Collectors.toList());
    }
}
