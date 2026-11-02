import { describe, expect, it } from "vitest";
import { formatDate } from "./format-date";

describe("formatDate", () => {
  it("formats in Spanish", () => {
    expect(formatDate("2026-03-15T12:00:00Z", "es")).toBe("15 de marzo de 2026");
  });

  it("formats in English", () => {
    expect(formatDate("2026-03-15T12:00:00Z", "en")).toBe("March 15, 2026");
  });
});
