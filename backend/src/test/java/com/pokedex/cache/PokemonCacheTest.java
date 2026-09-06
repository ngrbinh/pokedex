package com.pokedex.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.pokedex.api.PokeApiClient;
import com.pokedex.api.PokeApiModels.Pokemon;
import com.pokedex.api.PokeApiModels.Sprites;
import com.pokedex.config.PokedexProperties;
import java.time.Duration;
import org.junit.jupiter.api.Test;

/**
 * Tests cache hit and stale-value fallback behavior.
 */
class PokemonCacheTest {

    /** Verifies an unexpired entry avoids duplicate source loads. */
    @Test
    void returnsCachedValueWithoutCallingLoaderAgain() {
        PokeApiClient client = mock(PokeApiClient.class);
        when(client.getPokemon("bulbasaur")).thenReturn(new Pokemon(1, "bulbasaur", new Sprites(null, null), null));
        PokemonCache cache = createCache(client, Duration.ofHours(1));

        assertThat(cache.getPokemon("bulbasaur").id()).isEqualTo(1);
        assertThat(cache.getPokemon("bulbasaur").id()).isEqualTo(1);

        verify(client, times(1)).getPokemon("bulbasaur");
    }

    /** Verifies an expired entry is still returned if its replacement cannot load. */
    @Test
    void returnsStaleValueWhenRefreshFails() {
        PokeApiClient client = mock(PokeApiClient.class);
        when(client.getPokemon("bulbasaur")).thenReturn(new Pokemon(1, "bulbasaur", new Sprites(null, null), null));
        PokemonCache cache = createCache(client, Duration.ofMillis(-1));
        cache.getPokemon("bulbasaur");
        when(client.getPokemon("bulbasaur")).thenThrow(new IllegalStateException("offline"));

        assertThat(cache.getPokemon("bulbasaur").id()).isEqualTo(1);
    }

    private PokemonCache createCache(PokeApiClient client, Duration ttl) {
        PokedexProperties properties = new PokedexProperties();
        properties.getCache().setTtl(ttl);
        return new PokemonCache(client, properties);
    }
}