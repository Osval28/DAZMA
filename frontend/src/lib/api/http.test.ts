import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { ApiError, NetworkError } from "./errors";
import { apiCommand, apiJson } from "./http";

const BASE = "https://api.test";
const CSRF_URL = `${BASE}/security/api/v1/sessions/csrf`;

const fetchMock = vi.fn<typeof fetch>();

function jsonResponse(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), { status, headers: { "Content-Type": "application/json" } });
}

function setCsrfCookie(value: string) {
  document.cookie = `XSRF-TOKEN=${value}; path=/`;
}

function requestHeaders(callIndex: number): Record<string, string> {
  return (fetchMock.mock.calls[callIndex][1]?.headers ?? {}) as Record<string, string>;
}

describe("http client", () => {
  beforeEach(() => {
    document.cookie = "XSRF-TOKEN=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";
    vi.stubGlobal("fetch", fetchMock);
  });

  afterEach(() => {
    vi.unstubAllGlobals();
    fetchMock.mockReset();
  });

  it("sends a GET with credentials and without CSRF token", async () => {
    fetchMock.mockResolvedValueOnce(jsonResponse({ ok: true }));

    expect(await apiJson<{ ok: boolean }>("/things")).toEqual({ ok: true });

    expect(fetchMock).toHaveBeenCalledTimes(1);
    expect(fetchMock).toHaveBeenCalledWith(`${BASE}/things`, {
      method: "GET",
      headers: {},
      credentials: "include",
      body: undefined,
    });
  });

  it("sends a POST with the JSON body and the CSRF token of the cookie", async () => {
    setCsrfCookie("token-1");
    fetchMock.mockResolvedValueOnce(jsonResponse({ id: "1" }));

    await apiJson("/things", { method: "POST", body: { name: "Ana" } });

    expect(requestHeaders(0)).toEqual({ "Content-Type": "application/json", "X-XSRF-TOKEN": "token-1" });
    expect(fetchMock.mock.calls[0][1]?.body).toBe(JSON.stringify({ name: "Ana" }));
  });

  it("sends a DELETE without body but with CSRF token", async () => {
    setCsrfCookie("token-1");
    fetchMock.mockResolvedValueOnce(new Response(null, { status: 204 }));

    await apiCommand("/things/1", { method: "DELETE" });

    expect(requestHeaders(0)).toEqual({ "X-XSRF-TOKEN": "token-1" });
    expect(fetchMock.mock.calls[0][1]?.body).toBeUndefined();
  });

  it("asks for a CSRF token before a mutation when there is none", async () => {
    fetchMock.mockImplementation(async (url) => {
      if (String(url) === CSRF_URL) {
        setCsrfCookie("fresh");
        return new Response(null, { status: 204 });
      }
      return jsonResponse({});
    });

    await apiCommand("/things", { method: "POST", body: {} });

    expect(fetchMock.mock.calls.map(([url]) => String(url))).toEqual([CSRF_URL, `${BASE}/things`]);
    expect(requestHeaders(1)["X-XSRF-TOKEN"]).toBe("fresh");
  });

  it("refreshes the token and retries once when access is denied", async () => {
    setCsrfCookie("stale");
    let attempts = 0;
    fetchMock.mockImplementation(async (url) => {
      if (String(url) === CSRF_URL) {
        setCsrfCookie("fresh");
        return new Response(null, { status: 204 });
      }
      attempts += 1;
      return attempts === 1 ? jsonResponse({ code: "ACCESS_DENIED" }, 403) : jsonResponse({ done: true });
    });

    expect(await apiJson("/things", { method: "POST", body: {} })).toEqual({ done: true });

    expect(fetchMock).toHaveBeenCalledTimes(3);
    expect(requestHeaders(0)["X-XSRF-TOKEN"]).toBe("stale");
    expect(requestHeaders(2)["X-XSRF-TOKEN"]).toBe("fresh");
  });

  it("does not retry a 403 with another code", async () => {
    setCsrfCookie("token-1");
    fetchMock.mockResolvedValueOnce(jsonResponse({ code: "CHAT_EMAIL_NOT_VERIFIED" }, 403));

    const failure = await apiJson("/things", { method: "POST", body: {} }).catch((error: unknown) => error);

    expect(failure).toBeInstanceOf(ApiError);
    expect(failure).toMatchObject({ status: 403, code: "CHAT_EMAIL_NOT_VERIFIED" });
    expect(fetchMock).toHaveBeenCalledTimes(1);
  });

  it("does not retry a GET denied with ACCESS_DENIED", async () => {
    fetchMock.mockResolvedValueOnce(jsonResponse({ code: "ACCESS_DENIED" }, 403));

    await expect(apiJson("/things")).rejects.toMatchObject({ status: 403, code: "ACCESS_DENIED" });
    expect(fetchMock).toHaveBeenCalledTimes(1);
  });

  it("turns a problem response into an ApiError with its code", async () => {
    fetchMock.mockResolvedValueOnce(jsonResponse({ code: "USER_NOT_FOUND" }, 404));

    await expect(apiJson("/users/me")).rejects.toMatchObject({ status: 404, code: "USER_NOT_FOUND" });
  });

  it("uses a generic code when the error body is not JSON", async () => {
    fetchMock.mockResolvedValueOnce(new Response("<html>bad gateway</html>", { status: 502 }));

    await expect(apiJson("/things")).rejects.toMatchObject({ status: 502, code: "UNEXPECTED_RESPONSE" });
  });

  it("reports a network error when the request cannot be sent", async () => {
    fetchMock.mockRejectedValueOnce(new TypeError("Failed to fetch"));

    await expect(apiJson("/things")).rejects.toBeInstanceOf(NetworkError);
  });

  it("resolves a command without reading the body", async () => {
    fetchMock.mockResolvedValueOnce(new Response(null, { status: 204 }));

    await expect(apiCommand("/things")).resolves.toBeUndefined();
  });
});
