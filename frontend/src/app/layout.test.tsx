import { describe, expect, it, vi } from "vitest";

vi.mock("next/font/google", () => ({
  Space_Grotesk: () => ({ variable: "font-space" }),
  Bricolage_Grotesque: () => ({ variable: "font-bricolage" }),
}));
vi.mock("next-intl/server", () => ({
  getLocale: async () => "en",
  getTranslations: async (namespace: string) => (key: string) => `${namespace}.${key}`,
}));

import RootLayout, { generateMetadata } from "./layout";

describe("RootLayout", () => {
  it("describes the site", async () => {
    expect(await generateMetadata()).toEqual({ title: "Metadata.title", description: "Metadata.description" });
  });

  it("sets the language of the document and the fonts", async () => {
    const layout = await RootLayout({ children: <p>contenido</p> });

    expect(layout.type).toBe("html");
    expect(layout.props.lang).toBe("en");
    expect(layout.props.className).toContain("font-space");
    expect(layout.props.className).toContain("font-bricolage");
  });
});
