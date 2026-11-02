import { afterEach, describe, expect, it, vi } from "vitest";

const { set } = vi.hoisted(() => ({ set: vi.fn() }));

vi.mock("next/headers", () => ({
  cookies: async () => ({ set }),
}));

import { setLocale } from "./actions";

describe("setLocale", () => {
  afterEach(() => {
    vi.unstubAllEnvs();
  });

  it("saves a supported locale for a year", async () => {
    await setLocale("en");

    expect(set).toHaveBeenCalledWith("NEXT_LOCALE", "en", {
      path: "/",
      maxAge: 60 * 60 * 24 * 365,
      sameSite: "lax",
      secure: false,
    });
  });

  it("marks the cookie secure in production", async () => {
    vi.stubEnv("NODE_ENV", "production");

    await setLocale("es");

    expect(set).toHaveBeenCalledWith("NEXT_LOCALE", "es", expect.objectContaining({ secure: true }));
  });

  it("does not save an unsupported locale", async () => {
    await setLocale("fr");

    expect(set).not.toHaveBeenCalled();
  });
});
