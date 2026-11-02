import { FirebaseError } from "firebase/app";
import { beforeEach, describe, expect, it, vi } from "vitest";

const { sendPasswordResetEmail, auth } = vi.hoisted(() => ({
  sendPasswordResetEmail: vi.fn(),
  auth: { name: "auth" },
}));

vi.mock("firebase/auth", () => ({ sendPasswordResetEmail }));
vi.mock("@/lib/firebase/auth-client", () => ({ getFirebaseAuth: () => auth }));

import { PASSWORD_RESET_PATH, requestPasswordReset } from "./password-reset";

describe("requestPasswordReset", () => {
  beforeEach(() => {
    sendPasswordResetEmail.mockResolvedValue(undefined);
  });

  it("asks Firebase to send the reset email", async () => {
    await requestPasswordReset("ana@dazma.store");

    expect(sendPasswordResetEmail).toHaveBeenCalledWith(auth, "ana@dazma.store");
  });

  it("does not reveal that the account does not exist", async () => {
    sendPasswordResetEmail.mockRejectedValue(new FirebaseError("auth/user-not-found", "no user"));

    await expect(requestPasswordReset("nadie@dazma.store")).resolves.toBeUndefined();
  });

  it("propagates other Firebase failures", async () => {
    sendPasswordResetEmail.mockRejectedValue(new FirebaseError("auth/too-many-requests", "slow down"));

    await expect(requestPasswordReset("ana@dazma.store")).rejects.toMatchObject({ code: "auth/too-many-requests" });
  });

  it("propagates failures that are not Firebase errors", async () => {
    sendPasswordResetEmail.mockRejectedValue(new Error("offline"));

    await expect(requestPasswordReset("ana@dazma.store")).rejects.toThrow("offline");
  });

  it("exposes the recovery page path", () => {
    expect(PASSWORD_RESET_PATH).toBe("/recuperar-contrasena");
  });
});
