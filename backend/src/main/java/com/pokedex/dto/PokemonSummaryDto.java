package com.pokedex.dto;

import java.util.List;

/**
 * Pokemon fields displayed in the list view.
 */
public record PokemonSummaryDto(
	int id,
	String name,
	String frontImageUrl,
	List<String> types) { }