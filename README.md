# Pokedex

A Java Spring Boot and React application for browsing Pokemon from PokeAPI. The backend owns upstream requests, response mapping, caching, retry handling, and weakness calculation. The frontend provides an infinite-scroll list and a detail dialog.

## Structure

```text
backend/   Spring Boot API
frontend/  React and Vite application
ui_design/ Supplied interface references
```

## Prerequisites

- Java 21
- Node.js and npm

The repository includes Maven Wrapper, so a system Maven installation is not required.

## Run Locally

Start the backend from one terminal:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Start the frontend from another terminal:

```powershell
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

The backend runs at `http://localhost:8080`. Vite proxies `/api` to the backend, and the backend permits requests from the frontend development origin.

## API

| Endpoint | Description |
| --- | --- |
| `GET /api/pokemon?limit=5&offset=0` | Returns a paged list of Pokemon summaries. |
| `GET /api/pokemon/{idOrName}` | Returns detail data, including region, description, sprites, types, and weaknesses. |

Error responses contain a timestamp, HTTP status, and message. Invalid client input returns `400`; PokeAPI request or connectivity failures return `503`.

## Caching and Resilience

`PokemonCache` is a thread-safe cache facade over PokeAPI resources. It caches Pokemon, species, generation, and type data with a one-hour TTL. When a TTL refresh fails, a stale cached value is returned where available.

`PokeApiClient` is the only external HTTP boundary. It uses a five-second connection timeout, five-second read timeout, and two exponential-backoff retries beginning at 200 ms.

## Configuration

Configuration lives in [backend/src/main/resources/application.yml](backend/src/main/resources/application.yml).

| Property | Default | Purpose |
| --- | --- | --- |
| `server.port` | `8080` | Backend HTTP port. |
| `pokedex.frontend-url` | `http://localhost:5173` | Allowed development CORS origin. |
| `pokedex.page.default-size` | `5` | Initial frontend page size. |
| `pokedex.page.max-size` | `10` | Largest allowed API page size. |
| `pokedex.cache.ttl` | `1h` | Cache entry lifetime. |

Frontend configuration lives in `frontend/.env`. Copy `frontend/.env.example` when setting up a new environment.

| Property | Default | Purpose |
| --- | --- | --- |
| `VITE_POKEMON_PAGE_SIZE` | `5` | Number of Pokemon requested per page. |

## Checks

```powershell
cd backend
.\mvnw.cmd clean test

cd ..\frontend
npm run build
```