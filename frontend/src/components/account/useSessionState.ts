"use client";

import { useEffect, useState } from "react";
import { hasErrorCode } from "@/lib/api/errors";
import { getMyProfile } from "@/lib/api/users";

export type SessionState = { status: "loading" } | { status: "anonymous" } | { status: "member"; name: string | undefined };

const USER_NOT_FOUND_CODE = "USER_NOT_FOUND";

export function useSessionState(): SessionState {
  const [state, setState] = useState<SessionState>({ status: "loading" });

  useEffect(() => {
    let active = true;
    getMyProfile()
      .then((profile) => active && setState({ status: "member", name: profile.name }))
      .catch((error) => {
        if (active) {
          setState(hasErrorCode(error, USER_NOT_FOUND_CODE) ? { status: "member", name: undefined } : { status: "anonymous" });
        }
      });
    return () => {
      active = false;
    };
  }, []);

  return state;
}
