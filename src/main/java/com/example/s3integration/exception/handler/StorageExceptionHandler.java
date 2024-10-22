package com.example.s3integration.exception.handler;

import com.example.s3integration.exception.constants.StorageErrorConstants;
import com.example.s3integration.exception.custom.storage.StorageServiceException;
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
public class StorageExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(StorageExceptionHandler.class);

    /**
     * Handle StorageService errors.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(StorageServiceException.class)
    @ResponseBody
    public ApiError handleStorageServiceException(StorageServiceException ex) {
        logger.error("Storage Service operation failed: {}", ex.getMessage(), ex);
        return new ApiError(StorageErrorConstants.STORAGE_ERR001.code(), StorageErrorConstants.STORAGE_ERR001.message(), null);
    }
}
