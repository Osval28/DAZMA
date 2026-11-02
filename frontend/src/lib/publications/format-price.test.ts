import { describe, expect, it } from "vitest";
import { formatPrice } from "./format-price";

describe("formatPrice", () => {
  it("uses Colombian pesos with dot separators in Spanish", () => {
    expect(formatPrice(1500000, "es")).toMatch(/^\$\s?1\.500\.000$/);
  });

  it("uses comma separators in English", () => {
    expect(formatPrice(1500000, "en")).toMatch(/1,500,000$/);
  });

  it("drops the decimals", () => {
    expect(formatPrice(999.6, "es")).toMatch(/1\.000$/);
  });
});
