package com.example.demo.Validator;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import org.springframework.stereotype.Component;

@Component
public class LoginValidator implements Validator<String> {
    @Override
    public ValidationResult apply(String value, ValueContext valueContext) {
        if(value.length()>4){
            return ValidationResult.ok();
        }else {
            return  ValidationResult.error("Must have more than 5 characters");
        }
    }
}
