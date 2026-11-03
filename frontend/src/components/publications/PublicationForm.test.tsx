import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ApiError } from "@/lib/api/errors";
import { renderWithIntl, t } from "@/test/intl";

const mocks = vi.hoisted(() => {
  const replace = vi.fn();
  return {
    replace,
    router: { replace },
    createPublication: vi.fn(),
    getPublicationRules: vi.fn(),
    listMunicipalities: vi.fn(),
    drafts: { photos: [], uploadedIds: ["photo-1"], uploadingCount: 0, failedCount: 0, issue: undefined },
  };
});

vi.mock("next/navigation", () => ({ useRouter: () => mocks.router }));
vi.mock("@/lib/api/publications", () => ({ createPublication: mocks.createPublication, getPublicationRules: mocks.getPublicationRules }));
vi.mock("@/lib/api/municipalities", () => ({ listMunicipalities: mocks.listMunicipalities }));
vi.mock("./usePhotoDrafts", () => ({ usePhotoDrafts: () => mocks.drafts }));
vi.mock("./PhotoPicker", () => ({ PhotoPicker: () => <p>selector de fotos</p> }));

import { PublicationForm } from "./PublicationForm";

async function fillOffer({ price = "1.500.000" }: { price?: string } = {}) {
  await userEvent.type(screen.getByLabelText(t("Publications.create.nameLabel")), "iPhone 12");
  await userEvent.type(screen.getByLabelText(t("Publications.create.descriptionLabel")), "Casi nuevo");
  await userEvent.type(screen.getByLabelText(t("Publications.create.priceLabel")), price);
  await screen.findByRole("option", { name: "Rionegro" });
  await userEvent.selectOptions(screen.getByLabelText(t("Auth.profile.municipalityLabel")), "05615");
}

describe("PublicationForm", () => {
  beforeEach(() => {
    mocks.replace.mockReset();
    mocks.createPublication.mockReset();
    mocks.getPublicationRules.mockResolvedValue({
      maximumPhotos: 5,
      maximumPageSize: 50,
      defaultPageSize: 20,
      allowedPhotoContentTypes: ["image/png", "image/jpeg", "image/webp"],
      maximumPhotoBytes: 5242880,
    });
    mocks.drafts.uploadedIds = ["photo-1"];
    mocks.drafts.uploadingCount = 0;
    mocks.drafts.failedCount = 0;
    mocks.listMunicipalities.mockResolvedValue([{ code: "05615", name: "Rionegro", department: "Antioquia" }]);
  });

  it("rejects an incomplete draft without calling the API", async () => {
    renderWithIntl(<PublicationForm />);

    await userEvent.click(screen.getByRole("button", { name: t("Publications.create.submit") }));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Publications.create.errors.nameRequired"));
    expect(mocks.createPublication).not.toHaveBeenCalled();
  });

  it("publishes and opens the new publication", async () => {
    mocks.createPublication.mockResolvedValue({ id: "pub-9", status: "PUBLISHED", createdAt: "2026-11-01T10:00:00Z" });
    renderWithIntl(<PublicationForm />);
    await fillOffer();

    await userEvent.click(screen.getByRole("button", { name: t("Publications.create.submit") }));

    await waitFor(() => expect(mocks.replace).toHaveBeenCalledWith("/publicaciones/pub-9"));
    expect(mocks.createPublication).toHaveBeenCalledWith({
      name: "iPhone 12",
      type: "PURCHASE",
      description: "Casi nuevo",
      municipality: "05615",
      price: 1500000,
      photoUploadIds: ["photo-1"],
    });
  });

  it("publishes an exchange with a reference value", async () => {
    mocks.createPublication.mockResolvedValue({ id: "pub-9", status: "PUBLISHED", createdAt: "2026-11-01T10:00:00Z" });
    renderWithIntl(<PublicationForm />);
    await userEvent.click(screen.getByRole("radio", { name: t("Publications.create.typeOptions.EXCHANGE") }));
    await userEvent.type(screen.getByLabelText(t("Publications.create.nameLabel")), "iPhone 12");
    await userEvent.type(screen.getByLabelText(t("Publications.create.descriptionLabel")), "Cambio por consola");
    await userEvent.type(screen.getByLabelText(t("Publications.create.referenceValueLabel")), "800000");
    await screen.findByRole("option", { name: "Rionegro" });
    await userEvent.selectOptions(screen.getByLabelText(t("Auth.profile.municipalityLabel")), "05615");

    await userEvent.click(screen.getByRole("button", { name: t("Publications.create.submit") }));

    await waitFor(() => expect(mocks.createPublication).toHaveBeenCalledWith(expect.objectContaining({ type: "EXCHANGE", price: 800000 })));
  });

  it("asks the price as a reference value for exchanges", async () => {
    renderWithIntl(<PublicationForm />);

    expect(screen.getByLabelText(t("Publications.create.priceLabel"))).toBeInTheDocument();

    await userEvent.click(screen.getByRole("radio", { name: t("Publications.create.typeOptions.EXCHANGE") }));

    expect(screen.getByLabelText(t("Publications.create.referenceValueLabel"))).toBeInTheDocument();
  });

  it("waits for photos that are still uploading", async () => {
    mocks.drafts.uploadingCount = 1;
    renderWithIntl(<PublicationForm />);
    await fillOffer();

    await userEvent.click(screen.getByRole("button", { name: t("Publications.create.submit") }));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Publications.create.errors.photosUploading"));
    expect(mocks.createPublication).not.toHaveBeenCalled();
  });

  it("shows the server failure and lets the user retry", async () => {
    mocks.createPublication.mockRejectedValue(new ApiError(403, "PUBLICATION_OWNER_NOT_READY"));
    renderWithIntl(<PublicationForm />);
    await fillOffer();

    await userEvent.click(screen.getByRole("button", { name: t("Publications.create.submit") }));

    expect(await screen.findByRole("alert")).toHaveTextContent(t("Publications.create.errors.ownerNotReady"));
    expect(screen.getByRole("button", { name: t("Publications.create.submit") })).toBeEnabled();
    expect(mocks.replace).not.toHaveBeenCalled();
  });

  it("disables the form while publishing", async () => {
    mocks.createPublication.mockReturnValue(new Promise(() => undefined));
    renderWithIntl(<PublicationForm />);
    await fillOffer();

    await userEvent.click(screen.getByRole("button", { name: t("Publications.create.submit") }));

    expect(screen.getByRole("button", { name: t("Publications.create.submitting") })).toBeDisabled();
    expect(screen.getByLabelText(t("Publications.create.nameLabel"))).toBeDisabled();
  });
});
