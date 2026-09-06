package com.pokedex.dto;

import java.util.List;

/**
 * A paginated Pokemon summary response.
 */
public record PokemonPageDto(
	List<PokemonSummaryDto> items,
	int limit,
	int offset) { }