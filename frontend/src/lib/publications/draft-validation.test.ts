import { describe, expect, it } from "vitest";
import {
  MAXIMUM_DESCRIPTION_LENGTH,
  MAXIMUM_NAME_LENGTH,
  parsePrice,
  validateDraft,
  type PhotoProgress,
  type PublicationDraft,
} from "./draft-validation";

const validDraft: PublicationDraft = {
  name: "Celular",
  description: "Casi nuevo",
  price: "500.000",
  municipality: "05615",
};

const readyPhotos: PhotoProgress = { uploaded: 1, uploading: 0, failed: 0 };

describe("parsePrice", () => {
  it.each([
    ["500000", 500000],
    ["500.000", 500000],
    ["$ 1.500.000", 1500000],
    ["1,500,000", 1500000],
    ["2147483647", 2147483647],
    ["1", 1],
  ])("reads %j as %i", (text, expected) => {
    expect(parsePrice(text)).toBe(expected);
  });

  it.each(["", "0", "abc", "-5", "12a", "2147483648", "1.5e3"])("rejects %j", (text) => {
    expect(parsePrice(text)).toBeUndefined();
  });
});

describe("validateDraft", () => {
  it("accepts a complete draft", () => {
    expect(validateDraft(validDraft, readyPhotos)).toBeUndefined();
  });

  it("requires a name", () => {
    expect(validateDraft({ ...validDraft, name: "" }, readyPhotos)).toBe("NAME_REQUIRED");
  });

  it("limits the name length", () => {
    expect(validateDraft({ ...validDraft, name: "x".repeat(MAXIMUM_NAME_LENGTH) }, readyPhotos)).toBeUndefined();
    expect(validateDraft({ ...validDraft, name: "x".repeat(MAXIMUM_NAME_LENGTH + 1) }, readyPhotos)).toBe("NAME_TOO_LONG");
  });

  it("requires a description", () => {
    expect(validateDraft({ ...validDraft, description: "" }, readyPhotos)).toBe("DESCRIPTION_REQUIRED");
  });

  it("limits the description length", () => {
    const longest = "x".repeat(MAXIMUM_DESCRIPTION_LENGTH);
    expect(validateDraft({ ...validDraft, description: longest }, readyPhotos)).toBeUndefined();
    expect(validateDraft({ ...validDraft, description: longest + "x" }, readyPhotos)).toBe("DESCRIPTION_TOO_LONG");
  });

  it("requires a valid price", () => {
    expect(validateDraft({ ...validDraft, price: "gratis" }, readyPhotos)).toBe("PRICE_INVALID");
  });

  it("requires a municipality", () => {
    expect(validateDraft({ ...validDraft, municipality: "" }, readyPhotos)).toBe("MUNICIPALITY_REQUIRED");
  });

  it("waits for photos that are still uploading", () => {
    expect(validateDraft(validDraft, { uploaded: 1, uploading: 1, failed: 0 })).toBe("PHOTOS_UPLOADING");
  });

  it("rejects failed photos", () => {
    expect(validateDraft(validDraft, { uploaded: 1, uploading: 0, failed: 1 })).toBe("PHOTOS_FAILED");
  });

  it("requires at least one uploaded photo", () => {
    expect(validateDraft(validDraft, { uploaded: 0, uploading: 0, failed: 0 })).toBe("PHOTOS_REQUIRED");
  });

  it("reports the first problem when there are several", () => {
    expect(validateDraft({ ...validDraft, name: "", price: "x" }, { uploaded: 0, uploading: 0, failed: 0 })).toBe("NAME_REQUIRED");
  });
});
