import { describe, expect, it } from "vitest";
import { DESKTOP_COLUMNS } from "./view-columns";

describe("DESKTOP_COLUMNS", () => {
  it("offers the default among its options", () => {
    expect(DESKTOP_COLUMNS.options).toContain(DESKTOP_COLUMNS.defaultColumns);
  });

  it("has its own storage key", () => {
    expect(DESKTOP_COLUMNS.storageKey).toBe("dazma.listing.columns.desktop");
  });
});
