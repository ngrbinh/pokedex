package com.pokedex.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * JSON adapter models returned by PokeAPI.
 */
public final class PokeApiModels {

    private PokeApiModels() {
    }

    /** A named PokeAPI resource reference. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record NamedResource(String name, String url) { }

    /** A paged list of PokeAPI resource references. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Page(List<NamedResource> results) { }

    /** Pokemon resource fields required by this application. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Pokemon(int id, String name, Sprites sprites, List<PokemonType> types) { }

    /** Front and back sprite URLs. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Sprites(
        @JsonProperty("front_default") String frontDefault,
        @JsonProperty("back_default") String backDefault) { }

    /** A type assigned to a Pokemon. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonType(int slot, NamedResource type) { }

    /** Species fields used for generation and description lookup. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Species(
        NamedResource generation,
        @JsonProperty("flavor_text_entries") List<FlavorText> flavorTextEntries) { }

    /** A localized species description. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FlavorText(
        @JsonProperty("flavor_text") String flavorText,
        NamedResource language) { }

    /** Generation fields used for region lookup. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Generation(@JsonProperty("main_region") NamedResource mainRegion) { }

    /** Type fields used for weakness lookup. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Type(@JsonProperty("damage_relations") DamageRelations damageRelations) { }

    /** Damage relations that identify attacking types with double damage. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DamageRelations(
        @JsonProperty("double_damage_from") List<NamedResource> doubleDamageFrom) { }
}