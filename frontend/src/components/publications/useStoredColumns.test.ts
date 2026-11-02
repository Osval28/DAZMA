import { act, renderHook } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { DESKTOP_COLUMNS } from "@/lib/publications/view-columns";
import { useStoredColumns } from "./useStoredColumns";

describe("useStoredColumns", () => {
  beforeEach(() => {
    window.localStorage.clear();
  });

  it("uses the default when nothing is stored", () => {
    const { result } = renderHook(() => useStoredColumns(DESKTOP_COLUMNS));

    expect(result.current[0]).toBe(DESKTOP_COLUMNS.defaultColumns);
  });

  it("restores a stored preference", () => {
    window.localStorage.setItem(DESKTOP_COLUMNS.storageKey, "3");

    const { result } = renderHook(() => useStoredColumns(DESKTOP_COLUMNS));

    expect(result.current[0]).toBe(3);
  });

  it("ignores a stored value that is not an option", () => {
    window.localStorage.setItem(DESKTOP_COLUMNS.storageKey, "9");

    const { result } = renderHook(() => useStoredColumns(DESKTOP_COLUMNS));

    expect(result.current[0]).toBe(DESKTOP_COLUMNS.defaultColumns);
  });

  it("stores and applies a new preference", () => {
    const { result } = renderHook(() => useStoredColumns(DESKTOP_COLUMNS));

    act(() => result.current[1](5));

    expect(result.current[0]).toBe(5);
    expect(window.localStorage.getItem(DESKTOP_COLUMNS.storageKey)).toBe("5");
  });

  it("follows changes made from another tab", () => {
    const { result } = renderHook(() => useStoredColumns(DESKTOP_COLUMNS));

    act(() => {
      window.localStorage.setItem(DESKTOP_COLUMNS.storageKey, "2");
      window.dispatchEvent(new Event("storage"));
    });

    expect(result.current[0]).toBe(2);
  });

  it("falls back to the default when the storage cannot be read", () => {
    vi.spyOn(Storage.prototype, "getItem").mockImplementation(() => {
      throw new Error("blocked");
    });

    const { result } = renderHook(() => useStoredColumns(DESKTOP_COLUMNS));

    expect(result.current[0]).toBe(DESKTOP_COLUMNS.defaultColumns);
  });

  it("keeps working when the storage cannot be written", () => {
    vi.spyOn(Storage.prototype, "setItem").mockImplementation(() => {
      throw new Error("full");
    });
    const { result } = renderHook(() => useStoredColumns(DESKTOP_COLUMNS));

    act(() => result.current[1](5));

    expect(result.current[0]).toBe(DESKTOP_COLUMNS.defaultColumns);
  });
});
