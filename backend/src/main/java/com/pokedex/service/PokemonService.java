package com.pokedex.service;

import com.pokedex.api.PokeApiModels;
import com.pokedex.cache.PokemonCache;
import com.pokedex.dto.PokemonDetailDto;
import com.pokedex.dto.PokemonPageDto;
import com.pokedex.dto.PokemonSummaryDto;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Maps PokeAPI resources to the Pokedex API response models.
 */
@Service
public class PokemonService {

    private final PokemonCache cache;

        public PokemonService(PokemonCache cache) {
        this.cache = cache;
    }

    /** Returns a page of Pokemon summary cards. */
    public PokemonPageDto list(int limit, int offset) {
        PokeApiModels.Page page = cache.listPokemon(limit, offset);
        List<PokemonSummaryDto> items = page.results().stream()
                .map(PokeApiModels.NamedResource::name)
                .map(cache::getPokemon)
                .map(this::toPokemonSummary)
                .toList();
        return new PokemonPageDto(items, limit, offset);
    }

    /** Returns a Pokemon detail card enriched with species and type data. */
    public PokemonDetailDto detail(String idOrName) {
        PokeApiModels.Pokemon pokemon = cache.getPokemon(idOrName);
        PokeApiModels.Species species = cache.getPokemonSpecies(pokemon.name());
        String generation = species.generation().name();
        PokeApiModels.Generation generationData = cache.getGeneration(generation);
        List<String> types = typeNames(pokemon);
        List<String> weaknesses = types.stream()
                .map(cache::getType)
                .flatMap(type -> type
                        .damageRelations().doubleDamageFrom().stream())
                .map(PokeApiModels.NamedResource::name)
                .distinct()
                .sorted()
                .toList();
        String description = species.flavorTextEntries().stream()
                .filter(entry -> "en".equals(entry.language().name()))
                .map(PokeApiModels.FlavorText::flavorText)
                .map(text -> text.replaceAll("[\\n\\f]", " "))
                .findFirst()
                .orElse("No English description is available.");
        return new PokemonDetailDto(
                pokemon.id(), pokemon.name(), pokemon.sprites().frontDefault(),
                pokemon.sprites().backDefault(), types, generationData.mainRegion().name(),
                description, weaknesses);
    }

        private PokemonSummaryDto toPokemonSummary(PokeApiModels.Pokemon pokemon) {
        return new PokemonSummaryDto(
                pokemon.id(), pokemon.name(), pokemon.sprites().frontDefault(), typeNames(pokemon));
    }

    private List<String> typeNames(PokeApiModels.Pokemon pokemon) {
        return pokemon.types().stream()
                .sorted(Comparator.comparingInt(PokeApiModels.PokemonType::slot))
                .map(type -> type.type().name())
                .toList();
    }
}