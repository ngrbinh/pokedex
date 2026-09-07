import type { PokemonSummary } from "../../api/pokemonTypes";
import { TypeBadge } from "./TypeBadge";

type SummaryCardProps = {
  pokemon: PokemonSummary;
  onSelect: () => void;
};

export function SummaryCard({ pokemon, onSelect }: SummaryCardProps) {
  return (
    <button className="summary-card" onClick={onSelect} type="button">
      <span className="sprite sprite-summary">
        {pokemon.frontImageUrl ? (
          <img src={pokemon.frontImageUrl} alt="" />
        ) : (
          <span>Image unavailable</span>
        )}
      </span>
      <span className="summary-copy">
        <span className="number">#{String(pokemon.id).padStart(4, "0")}</span>
        <strong>{pokemon.name}</strong>
        <span className="badges">
          {pokemon.types.map((type) => (
            <TypeBadge key={type} type={type} />
          ))}
        </span>
      </span>
    </button>
  );
}