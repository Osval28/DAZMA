import { describe, expect, it } from "vitest";
import { initialOf } from "./initial";

describe("initialOf", () => {
  it("returns the uppercase first letter", () => {
    expect(initialOf("ana")).toBe("A");
  });

  it("skips leading spaces", () => {
    expect(initialOf("  luis")).toBe("L");
  });

  it("uppercases accented letters", () => {
    expect(initialOf("érika")).toBe("É");
  });

  it("keeps a whole emoji as the initial", () => {
    expect(initialOf("😀 feliz")).toBe("😀");
  });

  it("returns an empty string for an empty or blank name", () => {
    expect(initialOf("")).toBe("");
    expect(initialOf("   ")).toBe("");
  });
});
