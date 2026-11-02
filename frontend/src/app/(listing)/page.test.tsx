import { screen } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import type { PublicationSummary } from "@/lib/api/publications";
import { renderWithIntl, t } from "@/test/intl";

const { listMunicipalities, listPublications } = vi.hoisted(() => ({ listMunicipalities: vi.fn(), listPublications: vi.fn() }));

vi.mock("@/lib/api/municipalities", () => ({ listMunicipalities }));
vi.mock("@/lib/api/publications", () => ({ listPublications }));
vi.mock("@/components/account/SiteHeader", () => ({ SiteHeader: () => <header /> }));
vi.mock("@/components/brand/IconBackdrop", () => ({ IconBackdrop: () => <div /> }));
vi.mock("@/components/publications/MunicipalityFilter", () => ({
  MunicipalityFilter: ({ selected }: { selected?: string }) => <p>filtro {selected ?? "todos"}</p>,
}));

import HomePage from "./page";

function publication(id: string, name: string): PublicationSummary {
  return { id, name, type: "PURCHASE", municipality: "05615", price: 100000, coverUrl: `https://images.test/${id}.jpg`, createdAt: "2026-03-15T12:00:00Z" };
}

describe("HomePage", () => {
  beforeEach(() => {
    listMunicipalities.mockReset();
    listPublications.mockReset();
    listMunicipalities.mockResolvedValue([{ code: "05615", name: "Rionegro", department: "Antioquia" }]);
    listPublications.mockResolvedValue({ items: [publication("1", "iPhone")], page: 0, size: 20, hasNext: false });
  });

  it("lists the first page of publications with the municipality names", async () => {
    renderWithIntl(await HomePage({ searchParams: Promise.resolve({}) }));

    expect(listPublications).toHaveBeenCalledWith({ municipality: undefined, page: 0 });
    expect(screen.getByRole("heading", { name: "iPhone" })).toBeInTheDocument();
    expect(screen.getByText("Rionegro")).toBeInTheDocument();
    expect(screen.getByText("filtro todos")).toBeInTheDocument();
  });

  it("applies the municipality and page of the address", async () => {
    listPublications.mockResolvedValue({ items: [publication("1", "iPhone")], page: 2, size: 20, hasNext: true });

    renderWithIntl(await HomePage({ searchParams: Promise.resolve({ municipality: "05615", page: "2" }) }));

    expect(listPublications).toHaveBeenCalledWith({ municipality: "05615", page: 2 });
    expect(screen.getByText("filtro 05615")).toBeInTheDocument();
    expect(screen.getByRole("navigation", { name: t("Publications.pagination.label") })).toBeInTheDocument();
  });

  it("invites to publish when nothing matches", async () => {
    listPublications.mockResolvedValue({ items: [], page: 0, size: 20, hasNext: false });

    renderWithIntl(await HomePage({ searchParams: Promise.resolve({}) }));

    expect(screen.getByText(t("Publications.listing.empty"))).toBeInTheDocument();
    expect(screen.queryByRole("navigation", { name: t("Publications.pagination.label") })).not.toBeInTheDocument();
  });

  it("ignores an invalid municipality or page", async () => {
    renderWithIntl(await HomePage({ searchParams: Promise.resolve({ municipality: "x", page: "-1" }) }));

    expect(listPublications).toHaveBeenCalledWith({ municipality: undefined, page: 0 });
  });
});
