import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { PokedexPage } from "./PokedexPage";
import "./styles.css";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <PokedexPage />
  </StrictMode>,
);
