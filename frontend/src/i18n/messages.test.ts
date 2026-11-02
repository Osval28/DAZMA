import { describe, expect, it } from "vitest";
import english from "../../messages/en.json";
import spanish from "../../messages/es.json";

function keyPaths(value: unknown, prefix = ""): string[] {
  if (typeof value !== "object" || value === null) {
    return [prefix];
  }
  return Object.entries(value).flatMap(([key, child]) => keyPaths(child, prefix === "" ? key : `${prefix}.${key}`));
}

describe("message catalogs", () => {
  it("define exactly the same keys in Spanish and English", () => {
    expect(keyPaths(english).sort()).toEqual(keyPaths(spanish).sort());
  });

  it("have no empty messages", () => {
    for (const catalog of [spanish, english]) {
      const empty = keyPaths(catalog).filter((path) => {
        const value = path.split(".").reduce<unknown>((node, key) => (node as Record<string, unknown>)[key], catalog);
        return value === "";
      });
      expect(empty).toEqual([]);
    }
  });
});
