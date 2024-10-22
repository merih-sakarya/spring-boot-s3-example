package com.example.s3integration.model.core.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails<T extends BaseError> {

    private ErrorType type;
    private List<T> details;
}
