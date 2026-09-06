package com.pokedex.web;

import com.pokedex.api.PokeApiClient;
import java.time.Instant;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converts API failures into consistent JSON error responses.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /** Handles invalid request parameters. */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> invalidRequest(IllegalArgumentException exception) {
        return error(400, exception.getMessage());
    }

    /** Handles upstream PokeAPI response failures. */
    @ExceptionHandler(PokeApiClient.PokeApiException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Map<String, Object> upstreamFailure(PokeApiClient.PokeApiException exception) {
        LOGGER.warn("PokeAPI returned an error", exception);
        return error(503, "Pokemon data is temporarily unavailable");
    }

    /** Handles exhausted PokeAPI connection retries. */
    @ExceptionHandler(PokeApiClient.PokeApiUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Map<String, Object> unavailable(PokeApiClient.PokeApiUnavailableException exception) {
        LOGGER.warn("PokeAPI request could not be completed", exception);
        return error(503, "Pokemon data is temporarily unavailable");
    }

    private Map<String, Object> error(int status, String message) {
        return Map.of("timestamp", Instant.now().toString(), "status", status, "message", message);
    }
}