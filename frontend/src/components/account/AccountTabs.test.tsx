import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import { AccountTabs } from "./AccountTabs";

const tabs = [
  { id: "data", label: "Datos", content: <p>contenido datos</p> },
  { id: "publications", label: "Publicaciones", content: <p>contenido publicaciones</p> },
  { id: "security", label: "Seguridad", content: <p>contenido seguridad</p> },
];

describe("AccountTabs", () => {
  it("shows the first tab selected", () => {
    render(<AccountTabs label="Secciones" tabs={tabs} />);

    expect(screen.getByRole("tablist", { name: "Secciones" })).toBeInTheDocument();
    expect(screen.getByRole("tab", { name: "Datos" })).toHaveAttribute("aria-selected", "true");
    expect(screen.getByRole("tabpanel")).toHaveTextContent("contenido datos");
  });

  it("changes the panel when another tab is clicked", async () => {
    render(<AccountTabs label="Secciones" tabs={tabs} />);

    await userEvent.click(screen.getByRole("tab", { name: "Seguridad" }));

    expect(screen.getByRole("tab", { name: "Seguridad" })).toHaveAttribute("aria-selected", "true");
    expect(screen.getByRole("tabpanel")).toHaveTextContent("contenido seguridad");
  });

  it("only the selected tab is in the tab order", () => {
    render(<AccountTabs label="Secciones" tabs={tabs} />);

    expect(screen.getByRole("tab", { name: "Datos" })).toHaveAttribute("tabindex", "0");
    expect(screen.getByRole("tab", { name: "Publicaciones" })).toHaveAttribute("tabindex", "-1");
  });

  it("moves with the arrow keys and wraps around", async () => {
    render(<AccountTabs label="Secciones" tabs={tabs} />);
    screen.getByRole("tab", { name: "Datos" }).focus();

    await userEvent.keyboard("{ArrowRight}");
    expect(screen.getByRole("tab", { name: "Publicaciones" })).toHaveFocus();

    await userEvent.keyboard("{ArrowLeft}{ArrowLeft}");
    expect(screen.getByRole("tab", { name: "Seguridad" })).toHaveFocus();
    expect(screen.getByRole("tabpanel")).toHaveTextContent("contenido seguridad");
  });

  it("ignores other keys", async () => {
    render(<AccountTabs label="Secciones" tabs={tabs} />);
    screen.getByRole("tab", { name: "Datos" }).focus();

    await userEvent.keyboard("{ArrowDown}a");

    expect(screen.getByRole("tab", { name: "Datos" })).toHaveAttribute("aria-selected", "true");
  });
});
