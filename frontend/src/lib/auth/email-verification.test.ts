import { beforeEach, describe, expect, it, vi } from "vitest";

const { getFirebaseAuth, openSession } = vi.hoisted(() => ({
  getFirebaseAuth: vi.fn(),
  openSession: vi.fn(),
}));

vi.mock("@/lib/firebase/auth-client", () => ({ getFirebaseAuth }));
vi.mock("@/lib/api/session", () => ({ openSession }));

import { refreshVerifiedSession } from "./email-verification";

describe("refreshVerifiedSession", () => {
  beforeEach(() => {
    openSession.mockResolvedValue(undefined);
  });

  it("does nothing when nobody is signed in", async () => {
    getFirebaseAuth.mockReturnValue({ currentUser: null });

    expect(await refreshVerifiedSession()).toBe(false);
    expect(openSession).not.toHaveBeenCalled();
  });

  it("reloads the user and opens a session with a fresh token", async () => {
    const user = { reload: vi.fn().mockResolvedValue(undefined), getIdToken: vi.fn().mockResolvedValue("fresh-token") };
    getFirebaseAuth.mockReturnValue({ currentUser: user });

    expect(await refreshVerifiedSession()).toBe(true);

    expect(user.reload).toHaveBeenCalled();
    expect(user.getIdToken).toHaveBeenCalledWith(true);
    expect(openSession).toHaveBeenCalledWith("fresh-token");
  });
});
