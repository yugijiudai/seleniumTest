package com.lml.selenium.util;

import lombok.experimental.UtilityClass;
import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author yugi
 * @apiNote 校验的工具类
 * @since 2018-04-02
 */
@UtilityClass
public final class ValidationUtils {


    private final static Validator VALIDATOR = Validation.byProvider(HibernateValidator.class).configure().failFast(true).buildValidatorFactory().getValidator();

    /**
     * 校验对象
     */
    public <T> void validate(T obj) throws IllegalArgumentException {
        Set<ConstraintViolation<T>> constraintViolations = VALIDATOR.validate(obj);
        if (constraintViolations.size() > 0) {
            throw new IllegalArgumentException(constraintViolations.iterator().next().getMessage());
        }
    }
}
