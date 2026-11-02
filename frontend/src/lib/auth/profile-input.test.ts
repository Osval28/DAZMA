import { describe, expect, it } from "vitest";
import { readProfileInput } from "./profile-input";

function formWith(values: Record<string, string>): FormData {
  const form = new FormData();
  for (const [key, value] of Object.entries(values)) {
    form.set(key, value);
  }
  return form;
}

describe("readProfileInput", () => {
  it("trims the name and normalizes the phone", () => {
    const input = readProfileInput(formWith({ name: "  Ana  ", phone: "+57 (300) 123-4567", municipality: "05615" }));

    expect(input).toEqual({ name: "Ana", phone: "573001234567", municipality: "05615" });
  });

  it("keeps the municipality untouched", () => {
    const input = readProfileInput(formWith({ name: "Luis", phone: "3001234567", municipality: "05001" }));

    expect(input.municipality).toBe("05001");
  });
});
