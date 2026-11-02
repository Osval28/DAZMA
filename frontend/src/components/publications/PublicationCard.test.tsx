import { screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import type { PublicationSummary } from "@/lib/api/publications";
import { renderWithIntl, t } from "@/test/intl";
import { PublicationCard } from "./PublicationCard";

const publication: PublicationSummary = {
  id: "pub-1",
  name: "iPhone 12",
  type: "PURCHASE",
  municipality: "05615",
  price: 1500000,
  coverUrl: "https://images.test/cover.jpg",
  createdAt: "2026-03-15T12:00:00Z",
};

describe("PublicationCard", () => {
  it("links to the publication", () => {
    renderWithIntl(<PublicationCard publication={publication} municipalityName="Rionegro" />);

    expect(screen.getByRole("link")).toHaveAttribute("href", "/publicaciones/pub-1");
  });

  it("shows name, price, place and type", () => {
    renderWithIntl(<PublicationCard publication={publication} municipalityName="Rionegro" />);

    expect(screen.getByRole("heading", { name: "iPhone 12" })).toBeInTheDocument();
    expect(screen.getByText(/1\.500\.000/)).toBeInTheDocument();
    expect(screen.getByText("Rionegro")).toBeInTheDocument();
    expect(screen.getByText(t("Publications.types.PURCHASE"))).toHaveClass("badge--purchase");
  });

  it("describes the cover with the name of the publication", () => {
    renderWithIntl(<PublicationCard publication={publication} municipalityName="Rionegro" />);

    expect(screen.getByRole("img", { name: "iPhone 12" })).toBeInTheDocument();
  });

  it("marks exchanges and formats the price in English", () => {
    renderWithIntl(<PublicationCard publication={{ ...publication, type: "EXCHANGE" }} municipalityName="Rionegro" />, "en");

    expect(screen.getByText(/1,500,000/)).toBeInTheDocument();
    expect(document.querySelector(".badge--exchange")).not.toBeNull();
  });
});
