import { describe, expect, it, vi } from "vitest";

vi.mock("./http", () => ({ apiJson: vi.fn(), apiCommand: vi.fn() }));

import { apiCommand, apiJson } from "./http";
import { getMyProfile, registerUser } from "./users";

describe("users API", () => {
  it("reads the profile of the authenticated user", async () => {
    await getMyProfile();

    expect(apiJson).toHaveBeenCalledWith("/users/api/v1/users/me");
  });

  it("registers the user with the profile data", async () => {
    const input = { name: "Ana", phone: "3001234567", municipality: "05615" };

    await registerUser(input);

    expect(apiCommand).toHaveBeenCalledWith("/users/api/v1/users", { method: "POST", body: input });
  });
});
