package com.pokedex.dto;

import java.util.List;

/**
 * Complete Pokemon data displayed in the detail dialog.
 */
public record PokemonDetailDto(
    int id,
    String name,
    String frontImageUrl,
    String backImageUrl,
    List<String> types,
    String region,
    String description,
    List<String> weaknesses) { }