package com.localmarket.exception;

import com.localmarket.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void handleResourceNotFoundException_ReturnsNotFoundStatus() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Shop", "id", 1L);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Shop not found with id: '1'", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleDuplicateResourceException_ReturnsConflictStatus() {
        DuplicateResourceException ex = new DuplicateResourceException("User", "email", "test@example.com");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateResourceException(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("User already exists with email: 'test@example.com'", response.getBody().getMessage());
    }

    @Test
    void handleBadRequestException_ReturnsBadRequestStatus() {
        BadRequestException ex = new BadRequestException("Invalid price");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadRequestException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Invalid price", response.getBody().getMessage());
    }
}
