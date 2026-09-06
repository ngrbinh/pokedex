package com.pokedex.web;

import com.pokedex.config.PokedexProperties;
import com.pokedex.dto.PokemonDetailDto;
import com.pokedex.dto.PokemonPageDto;
import com.pokedex.service.PokemonService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes Pokemon list and detail resources to the frontend.
 */
@RestController
@RequestMapping("/api/pokemon")
@Validated
public class PokemonController {

    private final PokemonService service;
    private final PokedexProperties properties;

    public PokemonController(PokemonService service, PokedexProperties properties) {
        this.service = service;
        this.properties = properties;
    }

    /** Returns a validated page of Pokemon summaries. */
    @GetMapping
    public PokemonPageDto list(
            @RequestParam(defaultValue = "${pokedex.page.default-size}") @Min(1) @Max(100) int limit,
            @RequestParam(defaultValue = "0") @Min(0) int offset) {
        if (limit > properties.getPage().getMaxSize()) {
            throw new IllegalArgumentException(
                    "limit must not exceed " + properties.getPage().getMaxSize());
        }
        return service.list(limit, offset);
    }

    /** Returns detailed information for one Pokemon. */
    @GetMapping("/{idOrName}")
    public PokemonDetailDto detail(@PathVariable String idOrName) {
        if (!idOrName.matches("[a-zA-Z0-9-]+")) {
            throw new IllegalArgumentException("id must be a positive number or a Pokemon name");
        }
        return service.detail(idOrName);
    }
}