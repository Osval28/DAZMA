import { describe, expect, it } from "vitest";
import { formatMessageTime } from "./format-time";

describe("formatMessageTime", () => {
  it("shows hours and minutes", () => {
    expect(formatMessageTime(new Date(2026, 10, 1, 9, 5))).toMatch(/^0?9[:.]05/);
  });

  it("does not include the date", () => {
    expect(formatMessageTime(new Date(2026, 10, 1, 21, 30))).not.toMatch(/2026/);
  });
});
