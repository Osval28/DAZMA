"use client";

import { useState } from "react";
import type { AuthErrorKey } from "@/i18n/message-keys";
import { errorKey } from "@/lib/auth/errors";
import { isPopupDismissed, signInWithGoogle } from "@/lib/auth/sign-in";
import { Button } from "@/components/ui/Button";
import { GoogleIcon } from "@/components/ui/icons";

type GoogleButtonProps = {
  label: string;
  dividerLabel: string;
  disabled: boolean;
  onSignedIn: (destination: string) => void;
  onFailure: (failure: AuthErrorKey) => void;
};

export function GoogleButton({ label, dividerLabel, disabled, onSignedIn, onFailure }: GoogleButtonProps) {
  const [pending, setPending] = useState(false);

  async function handleClick() {
    setPending(true);
    try {
      onSignedIn(await signInWithGoogle());
    } catch (error) {
      if (!isPopupDismissed(error)) {
        onFailure(errorKey(error));
      }
      setPending(false);
    }
  }

  return (
    <>
      <div className="divider" role="separator">
        {dividerLabel}
      </div>
      <Button variant="secondary" disabled={disabled || pending} onClick={handleClick}>
        <GoogleIcon />
        {label}
      </Button>
    </>
  );
}
