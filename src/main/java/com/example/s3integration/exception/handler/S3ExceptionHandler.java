package com.example.s3integration.exception.handler;

import com.example.s3integration.exception.constants.S3ErrorConstants;
import com.example.s3integration.exception.custom.s3.S3ServiceException;
import com.example.s3integration.model.core.exception.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class S3ExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(S3ExceptionHandler.class);

    /**
     * Handle AWS S3 service errors.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(S3ServiceException.class)
    @ResponseBody
    public ApiError handleS3ServiceException(S3ServiceException ex) {
        logger.error("S3 Service operation failed: {}", ex.getMessage(), ex);
        return new ApiError(S3ErrorConstants.S3_ERR001.code(), S3ErrorConstants.S3_ERR001.message(), null);
    }
}
