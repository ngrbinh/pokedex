package com.pokedex.cache;

import com.pokedex.api.PokeApiClient;
import com.pokedex.api.PokeApiModels;
import com.pokedex.config.PokedexProperties;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

/**
 * Thread-safe resource cache with TTL refresh and stale-value fallback.
 */
@Component
public class PokemonCache {

    private final PokeApiClient client;
    private final ConcurrentHashMap<String, Entry<?>> values = new ConcurrentHashMap<>();
    private final Duration ttl;

    /**
     * Creates a cache facade for PokeAPI resources.
     *
     * @param client client used to load uncached resources
     * @param properties application configuration
     */
    public PokemonCache(PokeApiClient client, PokedexProperties properties) {
        this.client = client;
        this.ttl = properties.getCache().getTtl();
    }

    /**
     * Returns a cached page of Pokemon resource references.
     *
     * @param limit maximum number of resource references to return
     * @param offset number of resource references to skip
     * @return a cached or newly loaded PokeAPI page
     */
    public PokeApiModels.Page listPokemon(int limit, int offset) {
        return getCachedResource("pokemon-page", "%d:%d".formatted(limit, offset),
                () -> client.listPokemon(limit, offset));
    }

    /** Returns a cached Pokemon resource. */
    public PokeApiModels.Pokemon getPokemon(String idOrName) {
        return getCachedResource("pokemon", idOrName.toLowerCase(), () -> client.getPokemon(idOrName));
    }

    /** Returns a cached Pokemon species resource. */
    public PokeApiModels.Species getPokemonSpecies(String idOrName) {
        return getCachedResource("species", idOrName.toLowerCase(), () -> client.getPokemonSpecies(idOrName));
    }

    /** Returns a cached generation resource. */
    public PokeApiModels.Generation getGeneration(String idOrName) {
        return getCachedResource("generation", idOrName.toLowerCase(), () -> client.getGeneration(idOrName));
    }

    /** Returns a cached type resource. */
    public PokeApiModels.Type getType(String name) {
        return getCachedResource("type", name.toLowerCase(), () -> client.getType(name));
    }

    @SuppressWarnings("unchecked")
    private <T> T getCachedResource(String namespace, String key, Supplier<T> loader) {
        String cacheKey = namespace + ":" + key;
        Entry<T> entry = (Entry<T>) values.compute(cacheKey, (ignored, old) -> {
            if (old != null && !old.expired(Instant.now(), ttl)) {
                return old;
            }
            try {
                return new Entry<>(loader.get(), Instant.now());
            } catch (RuntimeException failure) {
                if (old != null) {
                    return old;
                }
                throw failure;
            }
        });
        return entry.value();
    }

    /** Clears all cached resource values. */
    public void clear() {
        values.clear();
    }

    private record Entry<T>(T value, Instant fetchedAt) {
        private boolean expired(Instant now, Duration ttl) {
            return fetchedAt.plus(ttl).isBefore(now);
        }
    }
}