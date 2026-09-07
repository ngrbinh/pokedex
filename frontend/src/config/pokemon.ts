const pokemonPageSize = Number.parseInt(
  import.meta.env.VITE_POKEMON_PAGE_SIZE,
  10,
);

if (!Number.isInteger(pokemonPageSize) || pokemonPageSize < 1) {
  throw new Error("VITE_POKEMON_PAGE_SIZE must be a positive integer.");
}

export { pokemonPageSize };