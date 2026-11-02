import { act, renderHook, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ApiError, NetworkError } from "@/lib/api/errors";

const { replace, router, getMyProfile } = vi.hoisted(() => {
  const replace = vi.fn();
  return { replace, router: { replace }, getMyProfile: vi.fn() };
});

vi.mock("next/navigation", () => ({ useRouter: () => router }));
vi.mock("@/lib/api/users", () => ({ getMyProfile }));

import { useSessionProfile } from "./useSessionProfile";

const profile = { id: "u1", name: "Ana", emailVerified: true };

describe("useSessionProfile", () => {
  beforeEach(() => {
    replace.mockReset();
    getMyProfile.mockReset();
  });

  it("starts loading", () => {
    getMyProfile.mockReturnValue(new Promise(() => undefined));

    const { result } = renderHook(() => useSessionProfile("/cuenta"));

    expect(result.current.state).toEqual({ status: "loading" });
  });

  it("exposes the profile once loaded", async () => {
    getMyProfile.mockResolvedValue(profile);

    const { result } = renderHook(() => useSessionProfile("/cuenta"));

    await waitFor(() => expect(result.current.state).toEqual({ status: "ready", profile }));
  });

  it("sends an unauthenticated visitor to sign in and back", async () => {
    getMyProfile.mockRejectedValue(new ApiError(401, "AUTHENTICATION_REQUIRED"));

    renderHook(() => useSessionProfile("/chats"));

    await waitFor(() => expect(replace).toHaveBeenCalledWith("/ingresar?next=%2Fchats"));
  });

  it("reports a missing profile", async () => {
    getMyProfile.mockRejectedValue(new ApiError(404, "USER_NOT_FOUND"));

    const { result } = renderHook(() => useSessionProfile("/cuenta"));

    await waitFor(() => expect(result.current.state).toEqual({ status: "missing" }));
  });

  it("reports a failure with its message key", async () => {
    getMyProfile.mockRejectedValue(new NetworkError());

    const { result } = renderHook(() => useSessionProfile("/cuenta"));

    await waitFor(() => expect(result.current.state).toEqual({ status: "failed", errorKey: "Auth.errors.network" }));
  });

  it("reloads the profile on demand", async () => {
    getMyProfile.mockResolvedValueOnce({ ...profile, emailVerified: false }).mockResolvedValueOnce(profile);
    const { result } = renderHook(() => useSessionProfile("/cuenta"));
    await waitFor(() => expect(result.current.state.status).toBe("ready"));

    await act(async () => {
      await result.current.reload();
    });

    expect(result.current.state).toEqual({ status: "ready", profile });
    expect(getMyProfile).toHaveBeenCalledTimes(2);
  });
});
