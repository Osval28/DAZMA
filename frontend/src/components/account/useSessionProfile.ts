"use client";

import { useRouter } from "next/navigation";
import { useCallback, useEffect, useState } from "react";
import type { AuthErrorKey } from "@/i18n/message-keys";
import { hasErrorCode, isUnauthenticated } from "@/lib/api/errors";
import { getMyProfile, type MyProfile } from "@/lib/api/users";
import { errorKey } from "@/lib/auth/errors";
import { LOGIN_PATH } from "@/lib/auth/sign-in";

export type SessionProfileState =
  | { status: "loading" }
  | { status: "ready"; profile: MyProfile }
  | { status: "missing" }
  | { status: "failed"; errorKey: AuthErrorKey };

const USER_NOT_FOUND_CODE = "USER_NOT_FOUND";

type SessionProfile = {
  state: SessionProfileState;
  reload: () => Promise<void>;
};

async function fetchState(): Promise<SessionProfileState | "unauthenticated"> {
  try {
    return { status: "ready", profile: await getMyProfile() };
  } catch (error) {
    if (isUnauthenticated(error)) {
      return "unauthenticated";
    }
    if (hasErrorCode(error, USER_NOT_FOUND_CODE)) {
      return { status: "missing" };
    }
    return { status: "failed", errorKey: errorKey(error) };
  }
}

export function useSessionProfile(returnPath: string): SessionProfile {
  const router = useRouter();
  const [state, setState] = useState<SessionProfileState>({ status: "loading" });

  const apply = useCallback(
    (result: SessionProfileState | "unauthenticated") => {
      if (result === "unauthenticated") {
        router.replace(`${LOGIN_PATH}?next=${encodeURIComponent(returnPath)}`);
      } else {
        setState(result);
      }
    },
    [returnPath, router],
  );

  const reload = useCallback(async () => apply(await fetchState()), [apply]);

  useEffect(() => {
    let active = true;
    fetchState().then((result) => active && apply(result));
    return () => {
      active = false;
    };
  }, [apply]);

  return { state, reload };
}
