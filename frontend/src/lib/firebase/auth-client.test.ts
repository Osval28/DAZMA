import { beforeEach, describe, expect, it, vi } from "vitest";

const mocks = vi.hoisted(() => ({
  getApps: vi.fn(),
  getApp: vi.fn(),
  initializeApp: vi.fn(),
  initializeAuth: vi.fn(),
}));

vi.mock("firebase/app", () => ({
  getApps: mocks.getApps,
  getApp: mocks.getApp,
  initializeApp: mocks.initializeApp,
}));
vi.mock("firebase/auth", () => ({
  initializeAuth: mocks.initializeAuth,
  inMemoryPersistence: "in-memory",
  browserPopupRedirectResolver: "resolver",
}));

describe("getFirebaseAuth", () => {
  beforeEach(() => {
    vi.resetModules();
    mocks.getApps.mockReturnValue([]);
    mocks.initializeApp.mockReturnValue({ name: "new-app" });
    mocks.getApp.mockReturnValue({ name: "existing-app" });
    mocks.initializeAuth.mockReturnValue({ name: "auth" });
  });

  it("initializes the app and the auth without persisting the session", async () => {
    const { getFirebaseAuth } = await import("./auth-client");

    expect(getFirebaseAuth()).toEqual({ name: "auth" });

    expect(mocks.initializeApp).toHaveBeenCalledWith({
      apiKey: "test-api-key",
      authDomain: "test.firebaseapp.com",
      projectId: "test-project",
      appId: "test-app-id",
    });
    expect(mocks.initializeAuth).toHaveBeenCalledWith(
      { name: "new-app" },
      { persistence: "in-memory", popupRedirectResolver: "resolver" },
    );
  });

  it("reuses an app that already exists", async () => {
    mocks.getApps.mockReturnValue([{ name: "existing-app" }]);
    const { getFirebaseAuth } = await import("./auth-client");

    getFirebaseAuth();

    expect(mocks.initializeApp).not.toHaveBeenCalled();
    expect(mocks.initializeAuth).toHaveBeenCalledWith({ name: "existing-app" }, expect.anything());
  });

  it("creates the auth only once", async () => {
    const { getFirebaseAuth } = await import("./auth-client");

    expect(getFirebaseAuth()).toBe(getFirebaseAuth());
    expect(mocks.initializeAuth).toHaveBeenCalledTimes(1);
  });
});
