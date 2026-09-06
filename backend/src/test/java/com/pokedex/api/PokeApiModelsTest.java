package com.pokedex.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests JSON mapping for PokeAPI adapter models.
 */
class PokeApiModelsTest {

    /** Verifies PokeAPI snake-case damage relation fields are mapped. */
    @Test
    void mapsTypeDamageRelations() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String typeResponse = """
                {
                  "damage_relations": {
                    "double_damage_from": [{ "name": "fire", "url": "https://pokeapi.co/api/v2/type/10/" }]
                  }
                }
                """;

        PokeApiModels.Type type = objectMapper.readValue(typeResponse, PokeApiModels.Type.class);

        assertThat(type.damageRelations().doubleDamageFrom())
                .extracting(PokeApiModels.NamedResource::name)
                .containsExactlyElementsOf(List.of("fire"));
    }
}