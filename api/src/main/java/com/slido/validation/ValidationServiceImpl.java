package com.slido.validation;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public class ValidationServiceImpl implements ValidationService {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Override
    public void validate(Object object, Class<?>... groups) {
        Set<ConstraintViolation<Object>> errors = validator.validate(object, groups);
        if(errors.size() > 0) {
            throw new ConstraintViolationException("Violations of object constraints", errors);
        }
    }
}
