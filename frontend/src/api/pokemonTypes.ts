export type PokemonSummary = {
  id: number;
  name: string;
  frontImageUrl: string | null;
  types: string[];
};

export type PokemonDetail = PokemonSummary & {
  backImageUrl: string | null;
  region: string;
  description: string;
  weaknesses: string[];
};

export type PokemonPage = {
  items: PokemonSummary[];
};