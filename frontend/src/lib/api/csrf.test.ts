import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { currentCsrfToken, refreshCsrfToken } from "./csrf";
import { CsrfUnavailableError, NetworkError } from "./errors";

const fetchMock = vi.fn<typeof fetch>();

function clearCsrfCookie() {
  document.cookie = "XSRF-TOKEN=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";
}

describe("CSRF token", () => {
  beforeEach(() => {
    clearCsrfCookie();
    vi.stubGlobal("fetch", fetchMock);
  });

  afterEach(() => {
    vi.unstubAllGlobals();
    fetchMock.mockReset();
  });

  it("reuses the token already stored in the cookie", async () => {
    document.cookie = "XSRF-TOKEN=cached-token; path=/";

    expect(await currentCsrfToken()).toBe("cached-token");
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("decodes an encoded cookie value", async () => {
    document.cookie = `XSRF-TOKEN=${encodeURIComponent("a b/c")}; path=/`;

    expect(await currentCsrfToken()).toBe("a b/c");
  });

  it("requests a new token when the cookie is missing", async () => {
    fetchMock.mockImplementation(async () => {
      document.cookie = "XSRF-TOKEN=fresh-token; path=/";
      return new Response(null, { status: 204 });
    });

    expect(await currentCsrfToken()).toBe("fresh-token");
    expect(fetchMock).toHaveBeenCalledWith("https://api.test/security/api/v1/sessions/csrf", { credentials: "include" });
  });

  it("fails when the server does not deliver the cookie", async () => {
    fetchMock.mockResolvedValue(new Response(null, { status: 204 }));

    await expect(refreshCsrfToken()).rejects.toBeInstanceOf(CsrfUnavailableError);
  });

  it("reports a network error when the server cannot be reached", async () => {
    fetchMock.mockRejectedValue(new TypeError("failed"));

    await expect(refreshCsrfToken()).rejects.toBeInstanceOf(NetworkError);
  });
});
