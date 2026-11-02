import { FirebaseError } from "firebase/app";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ApiError } from "@/lib/api/errors";

const mocks = vi.hoisted(() => ({
  signInWithEmailAndPassword: vi.fn(),
  signInWithPopup: vi.fn(),
  createUserWithEmailAndPassword: vi.fn(),
  sendEmailVerification: vi.fn(),
  signOut: vi.fn(),
  openSession: vi.fn(),
  closeSession: vi.fn(),
  getMyProfile: vi.fn(),
  auth: { name: "auth" },
}));

vi.mock("firebase/auth", () => ({
  GoogleAuthProvider: class GoogleAuthProvider {},
  signInWithEmailAndPassword: mocks.signInWithEmailAndPassword,
  signInWithPopup: mocks.signInWithPopup,
  createUserWithEmailAndPassword: mocks.createUserWithEmailAndPassword,
  sendEmailVerification: mocks.sendEmailVerification,
  signOut: mocks.signOut,
}));
vi.mock("@/lib/firebase/auth-client", () => ({ getFirebaseAuth: () => mocks.auth }));
vi.mock("@/lib/api/session", () => ({ openSession: mocks.openSession, closeSession: mocks.closeSession }));
vi.mock("@/lib/api/users", () => ({ getMyProfile: mocks.getMyProfile }));

import {
  ACCOUNT_PATH,
  HOME_PATH,
  createAccount,
  destinationAfterSignIn,
  isPopupDismissed,
  signInWithEmail,
  signInWithGoogle,
  signOutCompletely,
} from "./sign-in";

const credential = { user: { getIdToken: vi.fn() } };

describe("sign in", () => {
  beforeEach(() => {
    credential.user.getIdToken.mockResolvedValue("id-token");
    mocks.signInWithEmailAndPassword.mockResolvedValue(credential);
    mocks.signInWithPopup.mockResolvedValue(credential);
    mocks.createUserWithEmailAndPassword.mockResolvedValue(credential);
    mocks.openSession.mockResolvedValue(undefined);
    mocks.closeSession.mockResolvedValue(undefined);
    mocks.getMyProfile.mockResolvedValue({});
  });

  it("sends registered users to the home page", async () => {
    expect(await destinationAfterSignIn()).toBe(HOME_PATH);
  });

  it("sends users without profile to complete their account", async () => {
    mocks.getMyProfile.mockRejectedValue(new ApiError(404, "USER_NOT_FOUND"));

    expect(await destinationAfterSignIn()).toBe(ACCOUNT_PATH);
  });

  it("propagates any other profile failure", async () => {
    mocks.getMyProfile.mockRejectedValue(new ApiError(500, "BOOM"));

    await expect(destinationAfterSignIn()).rejects.toMatchObject({ status: 500 });
  });

  it("opens the backend session after signing in with email", async () => {
    expect(await signInWithEmail("ana@dazma.store", "secret123")).toBe(HOME_PATH);

    expect(mocks.signInWithEmailAndPassword).toHaveBeenCalledWith(mocks.auth, "ana@dazma.store", "secret123");
    expect(mocks.openSession).toHaveBeenCalledWith("id-token");
  });

  it("opens the backend session after signing in with Google", async () => {
    expect(await signInWithGoogle()).toBe(HOME_PATH);

    expect(mocks.signInWithPopup).toHaveBeenCalledWith(mocks.auth, expect.anything());
    expect(mocks.openSession).toHaveBeenCalledWith("id-token");
  });

  it("creates the account, sends the verification email and opens the session", async () => {
    await createAccount("ana@dazma.store", "secret123");

    expect(mocks.createUserWithEmailAndPassword).toHaveBeenCalledWith(mocks.auth, "ana@dazma.store", "secret123");
    expect(mocks.sendEmailVerification).toHaveBeenCalledWith(credential.user);
    expect(mocks.openSession).toHaveBeenCalledWith("id-token");
  });

  it("closes the backend session before signing out of Firebase", async () => {
    const order: string[] = [];
    mocks.closeSession.mockImplementation(async () => void order.push("backend"));
    mocks.signOut.mockImplementation(async () => void order.push("firebase"));

    await signOutCompletely();

    expect(order).toEqual(["backend", "firebase"]);
  });

  it("recognises a dismissed popup", () => {
    expect(isPopupDismissed(new FirebaseError("auth/popup-closed-by-user", "closed"))).toBe(true);
    expect(isPopupDismissed(new FirebaseError("auth/cancelled-popup-request", "cancelled"))).toBe(true);
  });

  it("does not treat other failures as a dismissed popup", () => {
    expect(isPopupDismissed(new FirebaseError("auth/popup-blocked", "blocked"))).toBe(false);
    expect(isPopupDismissed(new Error("auth/popup-closed-by-user"))).toBe(false);
  });
});
