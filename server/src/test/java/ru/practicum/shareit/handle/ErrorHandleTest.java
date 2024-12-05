package ru.practicum.shareit.handle;

import org.junit.jupiter.api.Test;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.handle.model.ErrorResp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandleTest {

    private final ErrorHandle errorHandle = new ErrorHandle();

    @Test
    void handleNotFoundException() {
        NotFoundException exception = new NotFoundException("Resource not found");

        ErrorResp response = errorHandle.handleNotFoundException(exception);

        assertEquals("Resource not found", response.getError());
    }

    @Test
    void handleValidationException() {
        ValidationException exception = new ValidationException("Validation failed");

        ErrorResp response = errorHandle.handleValidationException(exception);

        assertEquals("Validation failed", response.getError());
    }

    @Test
    void handleConflictException() {
        ConflictException exception = new ConflictException("Conflict occurred");

        ErrorResp response = errorHandle.handleConflictException(exception);

        assertEquals("Conflict occurred", response.getError());
    }

    @Test
    void handleTypeMismatchException() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getMessage()).thenReturn("Type mismatch error");

        ErrorResp response = errorHandle.handleTypeMismatch(exception);

        assertEquals("Type mismatch error", response.getError());
    }
}