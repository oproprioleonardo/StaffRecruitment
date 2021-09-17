package com.leon.srecruit.utils.validators;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class CommandValidator {

    private final Pattern patternCommand = Pattern.compile("&[A-Za-z]+");

    public boolean isBotCommand(String text) {
        return patternCommand.matcher(text).matches();
    }

}
