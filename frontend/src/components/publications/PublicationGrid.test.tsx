import { screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import type { PublicationSummary } from "@/lib/api/publications";
import { renderWithIntl, t } from "@/test/intl";
import { PublicationGrid } from "./PublicationGrid";

function publication(id: string, name: string, municipality: string): PublicationSummary {
  return { id, name, type: "PURCHASE", municipality, price: 100000, coverUrl: `https://images.test/${id}.jpg`, createdAt: "2026-03-15T12:00:00Z" };
}

describe("PublicationGrid", () => {
  it("invites to publish when there is nothing to show", () => {
    renderWithIntl(<PublicationGrid publications={[]} municipalityNames={{}} />);

    expect(screen.getByText(t("Publications.listing.empty"))).toBeInTheDocument();
    expect(screen.getByRole("link", { name: t("Publications.listing.emptyAction") })).toHaveAttribute("href", "/publicaciones/nueva");
    expect(screen.queryByRole("list")).not.toBeInTheDocument();
  });

  it("shows one card per publication with the name of its municipality", () => {
    renderWithIntl(
      <PublicationGrid
        publications={[publication("1", "iPhone", "05615"), publication("2", "Portátil", "05001")]}
        municipalityNames={{ "05615": "Rionegro", "05001": "Medellín" }}
      />,
    );

    expect(screen.getAllByRole("listitem")).toHaveLength(2);
    expect(screen.getByRole("heading", { name: "iPhone" })).toBeInTheDocument();
    expect(screen.getByText("Rionegro")).toBeInTheDocument();
    expect(screen.getByText("Medellín")).toBeInTheDocument();
  });
});
