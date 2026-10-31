"use client";

import { useCallback, useSyncExternalStore } from "react";
import type { ColumnsPreference } from "@/lib/publications/view-columns";

const CHANGE_EVENT = "dazma:columns-changed";

function subscribe(listener: () => void): () => void {
  window.addEventListener("storage", listener);
  window.addEventListener(CHANGE_EVENT, listener);
  return () => {
    window.removeEventListener("storage", listener);
    window.removeEventListener(CHANGE_EVENT, listener);
  };
}

function readColumns({ storageKey, options, defaultColumns }: ColumnsPreference): number {
  try {
    const stored = Number(window.localStorage.getItem(storageKey));
    return options.includes(stored) ? stored : defaultColumns;
  } catch {
    return defaultColumns;
  }
}

function writeColumns({ storageKey }: ColumnsPreference, columns: number): void {
  try {
    window.localStorage.setItem(storageKey, String(columns));
  } catch {
    return;
  }
  window.dispatchEvent(new Event(CHANGE_EVENT));
}

export function useStoredColumns(preference: ColumnsPreference): [number, (columns: number) => void] {
  const columns = useSyncExternalStore(
    subscribe,
    () => readColumns(preference),
    () => preference.defaultColumns,
  );
  const setColumns = useCallback((value: number) => writeColumns(preference, value), [preference]);
  return [columns, setColumns];
}
