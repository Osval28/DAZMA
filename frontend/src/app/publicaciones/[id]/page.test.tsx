import { screen } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ApiError } from "@/lib/api/errors";
import type { PublicationDetail } from "@/lib/api/publications";
import { renderWithIntl, t } from "@/test/intl";

const mocks = vi.hoisted(() => ({
  getPublication: vi.fn(),
  listMunicipalities: vi.fn(),
  notFound: vi.fn(() => {
    throw new Error("NEXT_NOT_FOUND");
  }),
}));

vi.mock("next/navigation", () => ({ notFound: mocks.notFound }));
vi.mock("next-intl/server", async () => {
  const { t } = await import("@/test/intl");
  const translate = t as unknown as (key: string, values?: Record<string, string | number>) => string;
  return {
    getLocale: async () => "es",
    getTranslations: async (namespace: string) => (key: string, values?: Record<string, string | number>) => translate(`${namespace}.${key}`, values),
  };
});
vi.mock("@/lib/api/publications", () => ({ getPublication: mocks.getPublication }));
vi.mock("@/lib/api/municipalities", () => ({ listMunicipalities: mocks.listMunicipalities }));
vi.mock("@/components/account/SiteHeader", () => ({ SiteHeader: () => <header /> }));
vi.mock("@/components/chats/ContactSellerButton", () => ({
  ContactSellerButton: ({ publicationId }: { publicationId: string }) => <button type="button">contactar {publicationId}</button>,
}));
vi.mock("@/components/publications/PublicationGallery", () => ({
  PublicationGallery: ({ photos }: { photos: { url: string }[] }) => <p>galería {photos.map((photo) => photo.url).join(",")}</p>,
}));

import PublicationPage from "./page";

const publication: PublicationDetail = {
  id: "pub-1",
  name: "iPhone 12",
  ownerName: "Ana",
  type: "PURCHASE",
  description: "Casi nuevo",
  municipality: "05615",
  price: 1500000,
  status: "PUBLISHED",
  photos: [
    { id: "2", url: "https://images.test/2.jpg", position: 1 },
    { id: "1", url: "https://images.test/1.jpg", position: 0 },
  ],
  createdAt: "2026-03-15T12:00:00Z",
};

async function renderPage(overrides: Partial<PublicationDetail> = {}) {
  mocks.getPublication.mockResolvedValue({ ...publication, ...overrides });
  renderWithIntl(await PublicationPage({ params: Promise.resolve({ id: "pub-1" }) }));
}

describe("PublicationPage", () => {
  beforeEach(() => {
    mocks.getPublication.mockReset();
    mocks.notFound.mockClear();
    mocks.listMunicipalities.mockResolvedValue([{ code: "05615", name: "Rionegro", department: "Antioquia" }]);
  });

  it("shows the publication", async () => {
    await renderPage();

    expect(mocks.getPublication).toHaveBeenCalledWith("pub-1");
    expect(screen.getByRole("heading", { name: "iPhone 12", level: 1 })).toBeInTheDocument();
    expect(screen.getByText(/1\.500\.000/)).toBeInTheDocument();
    expect(screen.getByText("Casi nuevo")).toBeInTheDocument();
    expect(screen.getByText("Rionegro")).toBeInTheDocument();
    expect(screen.getByText("15 de marzo de 2026")).toBeInTheDocument();
    expect(screen.getByText(t("Publications.types.PURCHASE"))).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "contactar pub-1" })).toBeInTheDocument();
  });

  it("names the seller", async () => {
    await renderPage();

    expect(screen.getByText("Ana")).toBeInTheDocument();
    expect(screen.queryByText(t("Publications.detail.sellerNotFound"))).not.toBeInTheDocument();
  });

  it("says so when the seller is unknown", async () => {
    await renderPage({ ownerName: "" });

    expect(screen.getByText(t("Publications.detail.sellerNotFound"))).toBeInTheDocument();
  });

  it("orders the photos by position", async () => {
    await renderPage();

    expect(screen.getByText("galería https://images.test/1.jpg,https://images.test/2.jpg")).toBeInTheDocument();
  });

  it("omits the gallery when there are no photos", async () => {
    await renderPage({ photos: [] });

    expect(screen.queryByText(/galería/)).not.toBeInTheDocument();
  });

  it("labels the price of an exchange as a reference value", async () => {
    await renderPage({ type: "EXCHANGE" });

    expect(screen.getByText(t("Publications.detail.referenceValue"))).toBeInTheDocument();
  });

  it("does not label the price of a sale", async () => {
    await renderPage();

    expect(screen.queryByText(t("Publications.detail.referenceValue"))).not.toBeInTheDocument();
  });

  it("shows the municipality code when its name is unknown", async () => {
    await renderPage({ municipality: "99999" });

    expect(screen.getByText("99999")).toBeInTheDocument();
  });

  it.each([400, 404])("shows the not found page for a %i", async (status) => {
    mocks.getPublication.mockRejectedValue(new ApiError(status, "PUBLICATION_NOT_FOUND"));

    await expect(PublicationPage({ params: Promise.resolve({ id: "pub-1" }) })).rejects.toThrow("NEXT_NOT_FOUND");
    expect(mocks.notFound).toHaveBeenCalled();
  });

  it("lets other failures reach the error page", async () => {
    mocks.getPublication.mockRejectedValue(new ApiError(500, "BOOM"));

    await expect(PublicationPage({ params: Promise.resolve({ id: "pub-1" }) })).rejects.toMatchObject({ status: 500 });
    expect(mocks.notFound).not.toHaveBeenCalled();
  });
});
