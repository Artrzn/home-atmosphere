package ru.home.atmosphere.processing;

import ru.home.atmosphere.AtmosphereControlException;

public class ProcessingException extends AtmosphereControlException {
    public ProcessingException(String message) {
        super(message);
    }

    public ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
