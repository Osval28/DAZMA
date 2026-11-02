import { describe, expect, it } from "vitest";
import { DEFAULT_LOCALE, LOCALES, formattingLocaleOf, isLocale, nextLocale } from "./config";

describe("locale configuration", () => {
  it("recognises the supported locales", () => {
    for (const locale of LOCALES) {
      expect(isLocale(locale)).toBe(true);
    }
  });

  it("rejects unsupported or missing locales", () => {
    expect(isLocale("fr")).toBe(false);
    expect(isLocale("")).toBe(false);
    expect(isLocale(undefined)).toBe(false);
  });

  it("cycles through the locales and wraps around", () => {
    expect(nextLocale("es")).toBe("en");
    expect(nextLocale("en")).toBe("es");
  });

  it("maps each locale to its formatting locale", () => {
    expect(formattingLocaleOf("es")).toBe("es-CO");
    expect(formattingLocaleOf("en")).toBe("en-US");
  });

  it("defaults to Spanish", () => {
    expect(DEFAULT_LOCALE).toBe("es");
  });
});
