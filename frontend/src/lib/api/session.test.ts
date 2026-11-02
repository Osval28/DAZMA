import { describe, expect, it, vi } from "vitest";

vi.mock("./http", () => ({ apiCommand: vi.fn() }));

import { apiCommand } from "./http";
import { closeSession, openSession } from "./session";

describe("session API", () => {
  it("opens the session with the identity token", async () => {
    await openSession("id-token");

    expect(apiCommand).toHaveBeenCalledWith("/security/api/v1/sessions", { method: "POST", body: { idToken: "id-token" } });
  });

  it("closes the session", async () => {
    await closeSession();

    expect(apiCommand).toHaveBeenCalledWith("/security/api/v1/sessions", { method: "DELETE" });
  });
});
