import type { PokemonSummary } from "../../api/pokemonTypes";
import { SummaryCard } from "./SummaryCard";

type PokemonListProps = {
  items: PokemonSummary[];
  isLoadingMore: boolean;
  onSelect: (id: number) => void;
  onScrollToBottom: () => void;
};

export function PokemonList({
  items,
  isLoadingMore,
  onSelect,
  onScrollToBottom,
}: PokemonListProps) {
  if (!items.length) {
    return <p className="state">No Pokemon were found.</p>;
  }

  return (
    <div
      className="pokemon-list-scroll"
      onScroll={(event) => {
        const listViewport = event.currentTarget;
        const remainingScrollDistance =
          listViewport.scrollHeight -
          listViewport.scrollTop -
          listViewport.clientHeight;

        if (remainingScrollDistance < 48) {
          onScrollToBottom();
        }
      }}
    >
      <div className="pokemon-list">
        {items.map((pokemon) => (
          <SummaryCard
            key={pokemon.id}
            pokemon={pokemon}
            onSelect={() => onSelect(pokemon.id)}
          />
        ))}
        {isLoadingMore && <p className="list-loading">Loading Pokemon...</p>}
      </div>
    </div>
  );
}