import { useEffect } from "react";
import type { PokemonDetail } from "../api";
import { DetailCard } from "./DetailCard";

type DetailModalProps = {
  pokemon: PokemonDetail | null;
  error: string | null;
  onClose: () => void;
};

export function DetailModal({ pokemon, error, onClose }: DetailModalProps) {
  useEffect(() => {
    const closeOnEscape = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        onClose();
      }
    };

    window.addEventListener("keydown", closeOnEscape);
    return () => window.removeEventListener("keydown", closeOnEscape);
  }, [onClose]);

  const closeOnBackdropClick = (event: React.MouseEvent<HTMLDivElement>) => {
    if (event.target === event.currentTarget) {
      onClose();
    }
  };

  return (
    <div className="modal-backdrop" onMouseDown={closeOnBackdropClick}>
      <div>
        {error ? (
          <div className="modal-error">
            <p>{error}</p>
            <button onClick={onClose}>Close</button>
          </div>
        ) : pokemon ? (
          <DetailCard pokemon={pokemon} onClose={onClose} />
        ) : (
          <p className="modal-loading">Loading details...</p>
        )}
      </div>
    </div>
  );
}
