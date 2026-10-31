import { config } from "@/lib/config";
import { CsrfUnavailableError, NetworkError } from "./errors";

const CSRF_COOKIE_NAME = "XSRF-TOKEN";
const CSRF_PATH = "/security/api/v1/sessions/csrf";

function readCsrfCookie(): string | undefined {
  const prefix = `${CSRF_COOKIE_NAME}=`;
  const entry = document.cookie.split("; ").find((item) => item.startsWith(prefix));
  return entry === undefined ? undefined : decodeURIComponent(entry.slice(prefix.length));
}

export async function refreshCsrfToken(): Promise<string> {
  try {
    await fetch(`${config.apiBaseUrl}${CSRF_PATH}`, { credentials: "include" });
  } catch {
    throw new NetworkError();
  }
  const token = readCsrfCookie();
  if (token === undefined) {
    throw new CsrfUnavailableError();
  }
  return token;
}

export async function currentCsrfToken(): Promise<string> {
  return readCsrfCookie() ?? (await refreshCsrfToken());
}
