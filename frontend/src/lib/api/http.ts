import { config } from "@/lib/config";
import { currentCsrfToken, refreshCsrfToken } from "./csrf";
import { ApiError, NetworkError, type ProblemDetail } from "./errors";

type HttpMethod = "GET" | "POST" | "DELETE";

type RequestOptions = {
  method?: HttpMethod;
  body?: unknown;
};

const ACCESS_DENIED_CODE = "ACCESS_DENIED";
const UNEXPECTED_RESPONSE_CODE = "UNEXPECTED_RESPONSE";

function isMutation({ method = "GET" }: RequestOptions): boolean {
  return method !== "GET";
}

async function readProblem(response: Response): Promise<ProblemDetail> {
  return (await response.json().catch(() => ({}))) as ProblemDetail;
}

async function isAccessDenied(response: Response): Promise<boolean> {
  if (response.status !== 403) {
    return false;
  }
  const problem = await readProblem(response.clone());
  return problem.code === ACCESS_DENIED_CODE;
}

async function dispatch(path: string, { method = "GET", body }: RequestOptions, csrfToken?: string): Promise<Response> {
  const headers: Record<string, string> = {};
  if (body !== undefined) {
    headers["Content-Type"] = "application/json";
  }
  if (csrfToken !== undefined) {
    headers["X-XSRF-TOKEN"] = csrfToken;
  }
  try {
    return await fetch(`${config.apiBaseUrl}${path}`, {
      method,
      headers,
      credentials: "include",
      body: body === undefined ? undefined : JSON.stringify(body),
    });
  } catch {
    throw new NetworkError();
  }
}

async function send(path: string, options: RequestOptions): Promise<Response> {
  const mutation = isMutation(options);
  let response = await dispatch(path, options, mutation ? await currentCsrfToken() : undefined);
  if (mutation && (await isAccessDenied(response))) {
    response = await dispatch(path, options, await refreshCsrfToken());
  }
  if (!response.ok) {
    const problem = await readProblem(response);
    throw new ApiError(response.status, problem.code ?? UNEXPECTED_RESPONSE_CODE);
  }
  return response;
}

export async function apiJson<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const response = await send(path, options);
  return (await response.json()) as T;
}

export async function apiCommand(path: string, options: RequestOptions = {}): Promise<void> {
  await send(path, options);
}
