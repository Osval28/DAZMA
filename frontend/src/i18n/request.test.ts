import { beforeEach, describe, expect, it, vi } from "vitest";

const { cookieStore, headerStore } = vi.hoisted(() => ({
  cookieStore: { get: vi.fn() },
  headerStore: { get: vi.fn() },
}));

vi.mock("next/headers", () => ({
  cookies: async () => cookieStore,
  headers: async () => headerStore,
}));

vi.mock("next-intl/server", () => ({
  getRequestConfig: (factory: unknown) => factory,
}));

import loadRequestConfig from "./request";

type ResolvedConfig = { locale: string; messages: Record<string, unknown> };

const resolve = loadRequestConfig as unknown as () => Promise<ResolvedConfig>;

describe("request locale resolution", () => {
  beforeEach(() => {
    cookieStore.get.mockReturnValue(undefined);
    headerStore.get.mockReturnValue(null);
  });

  it("uses the saved locale cookie first", async () => {
    cookieStore.get.mockReturnValue({ value: "en" });
    headerStore.get.mockReturnValue("es-CO,es;q=0.9");

    const config = await resolve();

    expect(config.locale).toBe("en");
    expect(config.messages).toHaveProperty("Chats");
  });

  it("ignores a cookie with an unsupported locale", async () => {
    cookieStore.get.mockReturnValue({ value: "fr" });
    headerStore.get.mockReturnValue("en-US,en;q=0.9");

    expect((await resolve()).locale).toBe("en");
  });

  it("falls back to the first supported language of Accept-Language", async () => {
    headerStore.get.mockReturnValue("fr-CA, en;q=0.8, es;q=0.5");

    expect((await resolve()).locale).toBe("en");
  });

  it("falls back to the default when Accept-Language is missing", async () => {
    expect((await resolve()).locale).toBe("es");
  });

  it("falls back to the default when no preferred language is supported", async () => {
    headerStore.get.mockReturnValue("fr-CA,de;q=0.8");

    expect((await resolve()).locale).toBe("es");
  });
});
