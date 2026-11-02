import { describe, expect, it } from "vitest";
import { AVATAR_TONES, toneOf } from "./avatar-tone";

describe("toneOf", () => {
  it("always returns a tone within range", () => {
    for (const name of ["Ana", "Luis", "María José", "😀", "", "x".repeat(300)]) {
      const tone = toneOf(name);
      expect(tone).toBeGreaterThanOrEqual(0);
      expect(tone).toBeLessThan(AVATAR_TONES);
    }
  });

  it("is stable for the same name", () => {
    expect(toneOf("Ana")).toBe(toneOf("Ana"));
  });

  it("ignores case and surrounding spaces", () => {
    expect(toneOf("  ANA ")).toBe(toneOf("ana"));
  });

  it("spreads different names over several tones", () => {
    const tones = new Set(["Ana", "Luis", "Marta", "Pedro", "Sofía", "Camilo", "Valentina", "Juan"].map(toneOf));
    expect(tones.size).toBeGreaterThan(1);
  });
});
