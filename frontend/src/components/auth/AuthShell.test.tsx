import { screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { renderWithIntl } from "@/test/intl";

vi.mock("@/i18n/actions", () => ({ setLocale: vi.fn() }));

import { AuthShell } from "./AuthShell";

describe("AuthShell", () => {
  it("frames the content with title, subtitle and footer", () => {
    renderWithIntl(
      <AuthShell title="Ingresar" subtitle="Bienvenida" footer={<a href="/registro">Crear cuenta</a>}>
        <p>formulario</p>
      </AuthShell>,
    );

    expect(screen.getByRole("heading", { name: "Ingresar" })).toBeInTheDocument();
    expect(screen.getByText("Bienvenida")).toBeInTheDocument();
    expect(screen.getByText("formulario")).toBeInTheDocument();
    expect(screen.getByRole("link", { name: "Crear cuenta" })).toBeInTheDocument();
  });

  it("is the main landmark of the page and lets the user change language", () => {
    renderWithIntl(
      <AuthShell title="Ingresar" subtitle="Bienvenida" footer="pie">
        <p>formulario</p>
      </AuthShell>,
    );

    expect(screen.getByRole("main")).toHaveAttribute("id", "contenido");
    expect(screen.getByRole("link", { name: "DAZMA" })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /EN|English|inglés/i })).toBeInTheDocument();
  });
});
