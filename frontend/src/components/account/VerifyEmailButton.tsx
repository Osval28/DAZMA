"use client";

import { useRouter } from "next/navigation";
import { useTranslations } from "next-intl";
import { useState } from "react";
import type { AuthErrorKey } from "@/i18n/message-keys";
import { errorKey } from "@/lib/auth/errors";
import { refreshVerifiedSession } from "@/lib/auth/email-verification";
import { LOGIN_PATH } from "@/lib/auth/sign-in";
import { Alert } from "@/components/ui/Alert";
import { Button } from "@/components/ui/Button";

type VerifyEmailButtonProps = {
  returnPath: string;
  onRefreshed: () => Promise<void>;
};

export function VerifyEmailButton({ returnPath, onRefreshed }: VerifyEmailButtonProps) {
  const t = useTranslations("Account.verifyEmail");
  const translateError = useTranslations();
  const router = useRouter();
  const [checking, setChecking] = useState(false);
  const [stillUnverified, setStillUnverified] = useState(false);
  const [failure, setFailure] = useState<AuthErrorKey>();

  async function handleClick() {
    setChecking(true);
    setStillUnverified(false);
    setFailure(undefined);
    try {
      const refreshed = await refreshVerifiedSession();
      if (!refreshed) {
        router.replace(`${LOGIN_PATH}?next=${encodeURIComponent(returnPath)}`);
        return;
      }
      await onRefreshed();
      setStillUnverified(true);
    } catch (error) {
      setFailure(errorKey(error));
    } finally {
      setChecking(false);
    }
  }

  return (
    <div className="verify-email">
      {failure !== undefined && <Alert>{translateError(failure)}</Alert>}
      {stillUnverified && (
        <p className="verify-email__hint" role="status">
          {t("stillUnverified")}
        </p>
      )}
      <Button type="button" variant="secondary" onClick={handleClick} disabled={checking}>
        {checking ? t("checking") : t("button")}
      </Button>
    </div>
  );
}
