"use client";

import { useEffect, useState } from "react";
import { hasErrorCode } from "@/lib/api/errors";
import { getMyProfile } from "@/lib/api/users";

export type SessionState = "loading" | "anonymous" | "member";

const USER_NOT_FOUND_CODE = "USER_NOT_FOUND";

export function useSessionState(): SessionState {
  const [state, setState] = useState<SessionState>("loading");

  useEffect(() => {
    let active = true;
    getMyProfile()
      .then(() => active && setState("member"))
      .catch((error) => active && setState(hasErrorCode(error, USER_NOT_FOUND_CODE) ? "member" : "anonymous"));
    return () => {
      active = false;
    };
  }, []);

  return state;
}
