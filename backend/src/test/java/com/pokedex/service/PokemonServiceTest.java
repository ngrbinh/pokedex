package com.pokedex.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.pokedex.api.PokeApiClient;
import com.pokedex.api.PokeApiModels.DamageRelations;
import com.pokedex.api.PokeApiModels.FlavorText;
import com.pokedex.api.PokeApiModels.Generation;
import com.pokedex.api.PokeApiModels.NamedResource;
import com.pokedex.api.PokeApiModels.Pokemon;
import com.pokedex.api.PokeApiModels.PokemonType;
import com.pokedex.api.PokeApiModels.Species;
import com.pokedex.api.PokeApiModels.Sprites;
import com.pokedex.api.PokeApiModels.Type;
import com.pokedex.cache.PokemonCache;
import com.pokedex.config.PokedexProperties;
import com.pokedex.dto.PokemonDetailDto;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests transformation of cached PokeAPI resources into detail responses.
 */
class PokemonServiceTest {

    /** Verifies enrichment, weakness deduplication, and reuse of warm cache values. */
    @Test
    void resolvesRegionDescriptionAndSortedDistinctWeaknesses() {
        PokeApiClient client = mock(PokeApiClient.class);
    Pokemon pokemon = new Pokemon(
        1,
        "bulbasaur",
        new Sprites("front", "back"),
        List.of(
            new PokemonType(1, new NamedResource("grass", "")),
            new PokemonType(2, new NamedResource("poison", ""))));

        when(client.getPokemon("1")).thenReturn(pokemon);
    when(client.getPokemonSpecies("bulbasaur")).thenReturn(new Species(
        new NamedResource("generation-i", ""),
        List.of(new FlavorText("A\nseed", new NamedResource("en", "")))));
        when(client.getGeneration("generation-i")).thenReturn(new Generation(new NamedResource("kanto", "")));
    when(client.getType("grass")).thenReturn(new Type(new DamageRelations(List.of(
        new NamedResource("fire", ""), new NamedResource("ice", "")))));
    when(client.getType("poison")).thenReturn(new Type(new DamageRelations(List.of(
        new NamedResource("ground", ""), new NamedResource("fire", "")))));

    PokedexProperties properties = new PokedexProperties();
    properties.getCache().setTtl(Duration.ofHours(1));
    PokemonService service = new PokemonService(new PokemonCache(client, properties));
        PokemonDetailDto detail = service.detail("1");

    assertThat(detail.region()).isEqualTo("kanto");
    assertThat(detail.description()).isEqualTo("A seed");
    assertThat(detail.weaknesses()).containsExactly("fire", "ground", "ice");

    service.detail("1");

    verify(client, times(1)).getPokemon("1");
    verify(client, times(1)).getType("grass");
    }
}