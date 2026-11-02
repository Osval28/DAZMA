import { renderHook, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ApiError } from "@/lib/api/errors";

const { getMyProfile } = vi.hoisted(() => ({ getMyProfile: vi.fn() }));

vi.mock("@/lib/api/users", () => ({ getMyProfile }));

import { useSessionState } from "./useSessionState";

describe("useSessionState", () => {
  beforeEach(() => {
    getMyProfile.mockReset();
  });

  it("starts loading", () => {
    getMyProfile.mockReturnValue(new Promise(() => undefined));

    const { result } = renderHook(() => useSessionState());

    expect(result.current).toEqual({ status: "loading" });
  });

  it("recognises a member by name", async () => {
    getMyProfile.mockResolvedValue({ name: "Ana" });

    const { result } = renderHook(() => useSessionState());

    await waitFor(() => expect(result.current).toEqual({ status: "member", name: "Ana" }));
  });

  it("treats a signed-in user without profile as a member without name", async () => {
    getMyProfile.mockRejectedValue(new ApiError(404, "USER_NOT_FOUND"));

    const { result } = renderHook(() => useSessionState());

    await waitFor(() => expect(result.current).toEqual({ status: "member", name: undefined }));
  });

  it("treats any other failure as anonymous", async () => {
    getMyProfile.mockRejectedValue(new ApiError(401, "AUTHENTICATION_REQUIRED"));

    const { result } = renderHook(() => useSessionState());

    await waitFor(() => expect(result.current).toEqual({ status: "anonymous" }));
  });

  it("does not update after unmounting", async () => {
    let resolve: (value: unknown) => void = () => undefined;
    getMyProfile.mockReturnValue(new Promise((done) => (resolve = done)));
    const { result, unmount } = renderHook(() => useSessionState());

    unmount();
    resolve({ name: "Ana" });
    await Promise.resolve();

    expect(result.current).toEqual({ status: "loading" });
  });
});
