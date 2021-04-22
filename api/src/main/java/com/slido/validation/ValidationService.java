package com.slido.validation;

public interface ValidationService {
    void validate(Object object, Class<?>... groups);
}
