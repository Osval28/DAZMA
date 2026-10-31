import { apiCommand } from "./http";

const SESSIONS_PATH = "/security/api/v1/sessions";

export function openSession(idToken: string): Promise<void> {
  return apiCommand(SESSIONS_PATH, { method: "POST", body: { idToken } });
}

export function closeSession(): Promise<void> {
  return apiCommand(SESSIONS_PATH, { method: "DELETE" });
}
