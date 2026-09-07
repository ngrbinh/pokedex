import type { PokemonDetail, PokemonPage } from "./pokemonTypes";

async function request<T>(path: string): Promise<T> {
  const response = await fetch(path);

  if (!response.ok) {
    const errorBody = await response.json().catch(() => null);
    throw new Error(errorBody?.message ?? "Unable to load Pokemon data.");
  }

  return response.json() as Promise<T>;
}

export const getPokemonList = (limit: number, offset: number) =>
  request<PokemonPage>(`/api/pokemon?limit=${limit}&offset=${offset}`);

export const getPokemonDetail = (id: number) =>
  request<PokemonDetail>(`/api/pokemon/${id}`);