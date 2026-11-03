import { describe, expect, it, vi } from "vitest";

vi.mock("./http", () => ({ apiJson: vi.fn() }));

import { apiJson } from "./http";
import { authorizePhotoUpload, createPublication, getPublication, getPublicationRules, listPublications } from "./publications";

describe("publications API", () => {
  it("creates a publication", async () => {
    const input = {
      name: "Celular",
      type: "PURCHASE" as const,
      description: "Casi nuevo",
      municipality: "05615",
      price: 500000,
      photoUploadIds: ["photo-1"],
    };

    await createPublication(input);

    expect(apiJson).toHaveBeenCalledWith("/publications/api/v1/publications", { method: "POST", body: input });
  });

  it("authorizes a photo upload with its type and size", async () => {
    await authorizePhotoUpload("image/png", 1234);

    expect(apiJson).toHaveBeenCalledWith("/publications/api/v1/photos/upload-authorizations", {
      method: "POST",
      body: { contentType: "image/png", contentLength: 1234 },
    });
  });

  it("reads a publication and encodes its id", async () => {
    await getPublication("a/b");

    expect(apiJson).toHaveBeenCalledWith("/publications/api/v1/publications/a%2Fb");
  });

  it("reads publication rules from the backend", async () => {
    await getPublicationRules();
    expect(apiJson).toHaveBeenCalledWith("/publications/api/v1/publications/rules");
  });

  it("lists a page without municipality filter", async () => {
    await listPublications({ page: 2 });

    expect(apiJson).toHaveBeenCalledWith("/publications/api/v1/publications?page=2");
  });

  it("lists a page filtered by municipality", async () => {
    await listPublications({ municipality: "05615", page: 0 });

    expect(apiJson).toHaveBeenCalledWith("/publications/api/v1/publications?page=0&municipality=05615");
  });
});
