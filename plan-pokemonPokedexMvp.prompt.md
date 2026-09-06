## Plan: Pokémon Pokédex MVP

Build a Java/Spring Boot backend and React frontend in one monorepo. The backend owns PokeAPI access, in-memory caching, data mapping, and weakness calculation. The frontend consumes stable DTOs and implements the supplied list/detail-card design.

**Steps**

1. Set up the Git repository and commit workflow:
   - Initialize a Git repository at the monorepo root if one does not already exist.
   - Add a root `.gitignore` covering Java/Maven, Node/npm, IDE, build, log, and environment files.
   - Add the initial plan and UI design files to the first commit.
   - Use one focused commit per plan step, created only after that step's implementation and checks pass.
   - Use commit subjects such as `chore: initialize repository`, `feat: scaffold applications`, and `test: add backend coverage`.
   - Do not combine unrelated changes or implementation steps in a single commit.

2. Create `backend/` and `frontend/` projects with shared configuration for:
   - PokeAPI base URL
   - HTTP timeouts and retries
   - Configurable page size, defaulting to 20
   - Cache TTL and refresh interval
   - Development CORS or frontend proxy

3. Define backend DTOs:
   - `PokemonSummaryDto`: ID, name, front image URL, types
   - `PokemonDetailDto`: summary fields, back image URL, region, description, weaknesses

4. Implement `PokeApiClient` as the only external HTTP boundary:
   - `listPokemon(limit, offset)`
   - `getPokemon(idOrName)`
   - `getPokemonSpecies(idOrName)`
   - `getGeneration(idOrName)`
   - `getType(name)`

5. Implement a thread-safe in-memory `PokemonCache` for Pokémon, species, generations, and types. Add TTL checks, stale-value fallback, refresh operations, and duplicate-fetch prevention.

6. Implement `PokemonService`:
   - Fetch paginated Pokémon names from PokeAPI.
   - Fetch and cache each Pokémon resource for summary data.
   - Fetch species and generation data for region and description.
   - Fetch and cache each distinct type.
   - Calculate weaknesses by unioning each type’s `double_damage_from` values.
   - Deduplicate and deterministically sort weakness names.

   A cold detail request for a two-type Pokémon requires at most:
   - 1 Pokémon request
   - 1 species request
   - 1 generation request
   - 2 type requests

7. Expose REST endpoints through `PokemonController`:
   - `GET /api/pokemon?limit=20&offset=0`
   - `GET /api/pokemon/{id}`

   Add pagination and ID validation, 404 handling, and consistent error responses.

8. Implement the React component tree:
   - `PokedexPage`
   - `PokemonList`
   - `SummaryCard`
   - `DetailModal`
   - `DetailCard`
   - `TypeBadge`

   Preserve the composition from [main_page.html](ui_design/main_page.html), [summary_card.html](ui_design/summary_card.html), [detail_card.html](ui_design/detail_card.html), and [type_badge.html](ui_design/type_badge.html).

9. Add UI states for loading, empty results, list errors, detail errors, missing images, modal close, backdrop click, and Escape-key handling.

10. Add backend-focused tests for:
   - PokeAPI response mapping
   - Cache hit, miss, TTL, and stale fallback
   - Weakness union and deduplication
   - Region and description resolution
   - Pagination validation
   - Controller success and error responses
   - Cold-cache versus warm-cache request counts

11. Verify the MVP by running backend tests and frontend build checks, then manually confirm the default 20-item list, detail modal fields, weaknesses, and cache reuse.

**Relevant files**

- [todo.txt](todo.txt) - requirements and selected decisions.
- [ui_design/main_page.html](ui_design/main_page.html) - list layout.
- [ui_design/summary_card.html](ui_design/summary_card.html) - summary card structure.
- [ui_design/detail_card.html](ui_design/detail_card.html) - detail modal, description, and weaknesses.
- [ui_design/type_badge.html](ui_design/type_badge.html) - reusable type badge.
- [ui_design/variable.txt](ui_design/variable.txt) - design tokens.

Planned backend classes:

- `PokeApiClient`
- `PokemonCache`
- `PokemonRefreshScheduler`
- `PokemonService`
- `PokemonController`
- API adapter models and public DTOs

Planned frontend components:

- `PokedexPage`
- `PokemonList`
- `SummaryCard`
- `DetailModal`
- `DetailCard`
- `TypeBadge`

**Decisions**

- Git history is part of the delivery: each plan step receives a separate focused commit after validation.
- Default page size: 20, configurable.
- Cache: thread-safe in-memory cache, periodically refreshed.
- Detail modal includes both description and weaknesses.
- Weaknesses derive from PokeAPI type damage relations.
- Backend owns all PokeAPI calls.
- Automated tests cover the backend only.
- No database, authentication, search, filtering, or full Pokémon preload in the MVP.

The full plan has also been saved to `/memories/session/plan.md`.