package com.example.demo.validator;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import org.springframework.stereotype.Component;

/**
 * i use  validators only to get visual error and notifcations
 * the real validations is done in Entity model by validation annotations
 *  the validantions rules in validators are the same as In Entity Classes
 *
 */
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
