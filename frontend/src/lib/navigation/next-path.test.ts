import { describe, expect, it } from "vitest";
import { safeNextPath } from "./next-path";

describe("safeNextPath", () => {
  it("keeps a local path with its query", () => {
    expect(safeNextPath("/publicaciones/nueva?x=1")).toBe("/publicaciones/nueva?x=1");
  });

  it("falls back to home without a candidate", () => {
    expect(safeNextPath(null)).toBe("/");
  });

  it.each(["https://evil.example", "//evil.example", "/\\evil.example", "evil", "javascript:alert(1)", ""])(
    "rejects the unsafe candidate %j",
    (candidate) => {
      expect(safeNextPath(candidate)).toBe("/");
    },
  );
});
