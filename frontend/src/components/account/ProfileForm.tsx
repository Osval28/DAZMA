"use client";

import { useRouter } from "next/navigation";
import { useTranslations } from "next-intl";
import { useState } from "react";
import type { FormEvent } from "react";
import type { AuthErrorKey } from "@/i18n/message-keys";
import { hasErrorCode } from "@/lib/api/errors";
import { registerUser } from "@/lib/api/users";
import { errorKey, validationIssueKey } from "@/lib/auth/errors";
import { readProfileInput } from "@/lib/auth/profile-input";
import { HOME_PATH } from "@/lib/auth/sign-in";
import { validateProfile } from "@/lib/validation";
import { Alert } from "@/components/ui/Alert";
import { Button } from "@/components/ui/Button";
import { ProfileFields } from "@/components/ui/ProfileFields";

const ALREADY_REGISTERED_CODE = "USER_ALREADY_REGISTERED";

export function ProfileForm() {
  const t = useTranslations();
  const router = useRouter();
  const [failure, setFailure] = useState<AuthErrorKey>();
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const profile = readProfileInput(new FormData(event.currentTarget));

    const issue = validateProfile(profile);
    if (issue !== undefined) {
      setFailure(validationIssueKey(issue));
      return;
    }

    setFailure(undefined);
    setSubmitting(true);
    try {
      await registerUser(profile);
      router.replace(HOME_PATH);
    } catch (error) {
      if (hasErrorCode(error, ALREADY_REGISTERED_CODE)) {
        router.replace(HOME_PATH);
        return;
      }
      setFailure(errorKey(error));
      setSubmitting(false);
    }
  }

  return (
    <form className="form" onSubmit={handleSubmit} noValidate>
      {failure !== undefined && <Alert>{t(failure)}</Alert>}
      <ProfileFields idPrefix="profile" />
      <Button type="submit" disabled={submitting}>
        {submitting ? t("Account.completeProfile.submitting") : t("Account.completeProfile.submit")}
      </Button>
    </form>
  );
}
