package com.example.demo.validator;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import org.springframework.stereotype.Component;

@Component
public class EmailValidator implements Validator<String> {
    @Override
    public ValidationResult apply(String value, ValueContext valueContext) {
        if (value.contains("@") && value.length() > 5) {
            return ValidationResult.ok();
        } else {
            return ValidationResult.error("wrong  email format");
        }

    }
}
