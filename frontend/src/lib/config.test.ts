import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

const variables = {
  NEXT_PUBLIC_API_BASE_URL: "https://api.test/",
  NEXT_PUBLIC_FIREBASE_API_KEY: "key",
  NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN: "domain",
  NEXT_PUBLIC_FIREBASE_PROJECT_ID: "project",
  NEXT_PUBLIC_FIREBASE_APP_ID: "app",
};

describe("config", () => {
  beforeEach(() => {
    vi.resetModules();
    for (const [name, value] of Object.entries(variables)) {
      vi.stubEnv(name, value);
    }
  });

  afterEach(() => {
    vi.unstubAllEnvs();
  });

  it("reads the environment and removes trailing slashes from the API url", async () => {
    const { config } = await import("./config");

    expect(config.apiBaseUrl).toBe("https://api.test");
    expect(config.firebase).toEqual({ apiKey: "key", authDomain: "domain", projectId: "project", appId: "app" });
  });

  it.each(Object.keys(variables))("fails at startup when %s is missing", async (name) => {
    vi.stubEnv(name, "");

    await expect(import("./config")).rejects.toThrow(`Falta la variable de entorno ${name}`);
  });
});
