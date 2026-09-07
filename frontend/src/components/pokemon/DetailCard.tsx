import type { ReactNode } from "react";
import { X } from "lucide-react";
import type { PokemonDetail } from "../../api/pokemonTypes";
import { TypeBadge } from "./TypeBadge";

type DetailCardProps = {
  pokemon: PokemonDetail;
  onClose: () => void;
};

type SpriteProps = {
  label: string;
  url: string | null;
};

type InfoProps = {
  label: string;
  children: ReactNode;
};

export function DetailCard({ pokemon, onClose }: DetailCardProps) {
  return (
    <section
      className="detail-card"
      role="dialog"
      aria-modal="true"
      aria-labelledby="pokemon-name"
    >
      <header>
        <div>
          <p className="number">#{String(pokemon.id).padStart(4, "0")}</p>
          <h2 id="pokemon-name">{pokemon.name}</h2>
          <div className="badges">
            {pokemon.types.map((type) => (
              <TypeBadge key={type} type={type} />
            ))}
          </div>
        </div>
        <button
          className="icon-button"
          aria-label="Close details"
          onClick={onClose}
        >
          <X size={19} />
        </button>
      </header>
      <div className="sprite-pair">
        <Sprite label="Front" url={pokemon.frontImageUrl} />
        <Sprite label="Back" url={pokemon.backImageUrl} />
      </div>
      <Info label="Region">
        <strong>{pokemon.region}</strong>
      </Info>
      <Info label="Description">
        <p>{pokemon.description}</p>
      </Info>
      <Info label="Weaknesses">
        <div className="badges">
          {pokemon.weaknesses.length ? (
            pokemon.weaknesses.map((type) => (
              <TypeBadge key={type} type={type} />
            ))
          ) : (
            <span className="empty-value">None</span>
          )}
        </div>
      </Info>
    </section>
  );
}

function Sprite({ label, url }: SpriteProps) {
  return (
    <div className="sprite sprite-detail">
      {url ? (
        <img src={url} alt={`${label} view`} />
      ) : (
        <span>Image unavailable</span>
      )}
      <small>{label}</small>
    </div>
  );
}

function Info({ label, children }: InfoProps) {
  return (
    <div className="info">
      <h3>{label}</h3>
      {children}
    </div>
  );
}