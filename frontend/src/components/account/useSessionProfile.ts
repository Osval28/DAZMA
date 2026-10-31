"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { hasErrorCode, isUnauthenticated } from "@/lib/api/errors";
import { getMyProfile, type MyProfile } from "@/lib/api/users";
import { describeError } from "@/lib/auth/errors";
import { LOGIN_PATH } from "@/lib/auth/sign-in";

export type SessionProfileState =
  | { status: "loading" }
  | { status: "ready"; profile: MyProfile }
  | { status: "missing" }
  | { status: "failed"; message: string };

const USER_NOT_FOUND_CODE = "USER_NOT_FOUND";

export function useSessionProfile(returnPath: string): SessionProfileState {
  const router = useRouter();
  const [state, setState] = useState<SessionProfileState>({ status: "loading" });

  useEffect(() => {
    let active = true;
    getMyProfile()
      .then((profile) => active && setState({ status: "ready", profile }))
      .catch((error) => {
        if (!active) {
          return;
        }
        if (isUnauthenticated(error)) {
          router.replace(`${LOGIN_PATH}?next=${encodeURIComponent(returnPath)}`);
        } else if (hasErrorCode(error, USER_NOT_FOUND_CODE)) {
          setState({ status: "missing" });
        } else {
          setState({ status: "failed", message: describeError(error) });
        }
      });
    return () => {
      active = false;
    };
  }, [returnPath, router]);

  return state;
}
