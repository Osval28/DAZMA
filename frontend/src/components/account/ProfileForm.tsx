"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import type { FormEvent } from "react";
import { accountMessages } from "@/lib/account/messages";
import { hasErrorCode } from "@/lib/api/errors";
import { registerUser } from "@/lib/api/users";
import { describeError, describeValidationIssue } from "@/lib/auth/errors";
import { readProfileInput } from "@/lib/auth/profile-input";
import { HOME_PATH } from "@/lib/auth/sign-in";
import { validateProfile } from "@/lib/validation";
import { Alert } from "@/components/ui/Alert";
import { Button } from "@/components/ui/Button";
import { ProfileFields } from "@/components/ui/ProfileFields";

const messages = accountMessages.completeProfile;
const ALREADY_REGISTERED_CODE = "USER_ALREADY_REGISTERED";

export function ProfileForm() {
  const router = useRouter();
  const [errorMessage, setErrorMessage] = useState<string>();
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const profile = readProfileInput(new FormData(event.currentTarget));

    const issue = validateProfile(profile);
    if (issue !== undefined) {
      setErrorMessage(describeValidationIssue(issue));
      return;
    }

    setErrorMessage(undefined);
    setSubmitting(true);
    try {
      await registerUser(profile);
      router.replace(HOME_PATH);
    } catch (error) {
      if (hasErrorCode(error, ALREADY_REGISTERED_CODE)) {
        router.replace(HOME_PATH);
        return;
      }
      setErrorMessage(describeError(error));
      setSubmitting(false);
    }
  }

  return (
    <form className="form" onSubmit={handleSubmit} noValidate>
      {errorMessage !== undefined && <Alert>{errorMessage}</Alert>}
      <ProfileFields idPrefix="profile" />
      <Button type="submit" disabled={submitting}>
        {submitting ? messages.submitting : messages.submit}
      </Button>
    </form>
  );
}
