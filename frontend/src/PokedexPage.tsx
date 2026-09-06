import { useEffect, useState } from "react";
import {
  getPokemonDetail,
  getPokemonList,
  type PokemonDetail,
  type PokemonSummary,
} from "./api";
import { DetailModal } from "./components/DetailModal";
import { PokemonList } from "./components/PokemonList";

const PAGE_SIZE = 5;

export function PokedexPage() {
  const [items, setItems] = useState<PokemonSummary[]>([]);
  const [listError, setListError] = useState<string | null>(null);
  const [isLoadingList, setIsLoadingList] = useState(true);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [hasMorePokemon, setHasMorePokemon] = useState(true);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [detail, setDetail] = useState<PokemonDetail | null>(null);
  const [detailError, setDetailError] = useState<string | null>(null);

  useEffect(() => {
    getPokemonList(PAGE_SIZE, 0)
      .then((page) => {
        setItems(page.items);
        setHasMorePokemon(page.items.length === PAGE_SIZE);
      })
      .catch((error: Error) => setListError(error.message))
      .finally(() => setIsLoadingList(false));
  }, []);

  useEffect(() => {
    if (selectedId === null) {
      return;
    }

    setDetail(null);
    setDetailError(null);
    getPokemonDetail(selectedId)
      .then(setDetail)
      .catch((error: Error) => setDetailError(error.message));
  }, [selectedId]);

  const closeDetail = () => setSelectedId(null);

  const loadMorePokemon = () => {
    if (isLoadingMore || !hasMorePokemon) {
      return;
    }

    setIsLoadingMore(true);
    getPokemonList(PAGE_SIZE, items.length)
      .then((page) => {
        setItems((currentItems) => [...currentItems, ...page.items]);
        setHasMorePokemon(page.items.length === PAGE_SIZE);
      })
      .catch((error: Error) => setListError(error.message))
      .finally(() => setIsLoadingMore(false));
  };

  return (
    <main className="page">
      <section className="feed">
        <header className="page-header">
          <h1>Pokédex</h1>
        </header>
        <div className="content">
          {listError ? (
            <p className="state error">{listError}</p>
          ) : isLoadingList ? (
            <p className="state">Loading Pokemon...</p>
          ) : items.length ? (
            <PokemonList
              items={items}
              isLoadingMore={isLoadingMore}
              onSelect={setSelectedId}
              onScrollToBottom={loadMorePokemon}
            />
          ) : (
            <p className="state">No Pokemon were found.</p>
          )}
        </div>
      </section>
      {selectedId !== null && (
        <DetailModal
          pokemon={detail}
          error={detailError}
          onClose={closeDetail}
        />
      )}
    </main>
  );
}
