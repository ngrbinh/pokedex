package com.pokedex.api;

import com.pokedex.config.PokedexProperties;
import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;

/**
 * The sole HTTP boundary for PokeAPI resources.
 */
@Component
public class PokeApiClient {

    private final RestClient restClient;
    private final int retries;
    private final Duration retryInitialDelay;

    /**
     * Creates a client using the configured PokeAPI connection settings.
     *
     * @param properties application configuration
     */
    public PokeApiClient(PokedexProperties properties) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.getPokeApi().getConnectTimeout())
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(properties.getPokeApi().getReadTimeout());
        this.restClient = RestClient.builder()
                .baseUrl(properties.getPokeApi().getBaseUrl())
                .requestFactory(requestFactory)
                .build();
        this.retries = properties.getPokeApi().getRetries();
        this.retryInitialDelay = properties.getPokeApi().getRetryInitialDelay();
    }

    /** Fetches one page of Pokemon resource references. */
    public PokeApiModels.Page listPokemon(int limit, int offset) {
        return get("/pokemon?limit={limit}&offset={offset}", new ParameterizedTypeReference<>() { }, limit, offset);
    }

    /** Fetches a Pokemon by numeric ID or name. */
    public PokeApiModels.Pokemon getPokemon(String idOrName) {
        return get("/pokemon/{idOrName}", new ParameterizedTypeReference<>() { }, idOrName);
    }

    /** Fetches a Pokemon species by numeric ID or name. */
    public PokeApiModels.Species getPokemonSpecies(String idOrName) {
        return get("/pokemon-species/{idOrName}", new ParameterizedTypeReference<>() { }, idOrName);
    }

    /** Fetches a generation by name or ID. */
    public PokeApiModels.Generation getGeneration(String idOrName) {
        return get("/generation/{idOrName}", new ParameterizedTypeReference<>() { }, idOrName);
    }

    /** Fetches a Pokemon type by name. */
    public PokeApiModels.Type getType(String name) {
        return get("/type/{name}", new ParameterizedTypeReference<>() { }, name);
    }

    private <T> T get(String uriTemplate, ParameterizedTypeReference<T> type, Object... variables) {
        RuntimeException last = null;
        for (int attempt = 0; attempt <= retries; attempt++) {
            try {
                return restClient.get()
                        .uri(uriTemplate, variables)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, (request, response) -> {
                            throw new PokeApiException(response.getStatusCode().value());
                        })
                        .body(type);
            } catch (PokeApiException | RestClientResponseException | ResourceAccessException exception) {
                last = exception;
                if (attempt < retries) {
                    waitBeforeRetry(attempt);
                }
            }
        }
        throw new PokeApiUnavailableException(last);
    }

    private void waitBeforeRetry(int attempt) {
        try {
            Thread.sleep(retryInitialDelay.multipliedBy(1L << attempt));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new PokeApiUnavailableException(exception);
        }
    }

    /** Indicates a response error returned by PokeAPI. */
    public static class PokeApiException extends RuntimeException {
        public PokeApiException(int status) {
            super("PokeAPI request failed with %d".formatted(status));
        }
    }

    /** Indicates PokeAPI could not be reached after all retry attempts. */
    public static class PokeApiUnavailableException extends RuntimeException {
        public PokeApiUnavailableException(Throwable cause) {
            super("PokeAPI is unavailable", cause);
        }
    }
}