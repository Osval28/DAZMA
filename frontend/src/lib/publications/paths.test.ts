import { describe, expect, it } from "vitest";
import { NEW_PUBLICATION_PATH, publicationPath } from "./paths";

describe("publication paths", () => {
  it("points to the creation page", () => {
    expect(NEW_PUBLICATION_PATH).toBe("/publicaciones/nueva");
  });

  it("builds and encodes the path of a publication", () => {
    expect(publicationPath("abc")).toBe("/publicaciones/abc");
    expect(publicationPath("a/b")).toBe("/publicaciones/a%2Fb");
  });
});
