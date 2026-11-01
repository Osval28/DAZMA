"use client";

import { useTranslations } from "next-intl";
import { useState } from "react";
import type { FormEvent } from "react";
import type { AuthErrorKey } from "@/i18n/message-keys";
import { errorKey, validationIssueKey } from "@/lib/auth/errors";
import { requestPasswordReset } from "@/lib/auth/password-reset";
import { validateEmail } from "@/lib/validation";
import { Alert } from "@/components/ui/Alert";
import { Button } from "@/components/ui/Button";
import { TextField } from "@/components/ui/TextField";

export function PasswordResetForm() {
  const t = useTranslations("Auth.passwordReset");
  const translateError = useTranslations();
  const [failure, setFailure] = useState<AuthErrorKey>();
  const [submitting, setSubmitting] = useState(false);
  const [requested, setRequested] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const email = String(new FormData(event.currentTarget).get("email")).trim();

    const issue = validateEmail(email);
    if (issue !== undefined) {
      setFailure(validationIssueKey(issue));
      return;
    }

    setFailure(undefined);
    setSubmitting(true);
    try {
      await requestPasswordReset(email);
      setRequested(true);
    } catch (error) {
      setFailure(errorKey(error));
    } finally {
      setSubmitting(false);
    }
  }

  if (requested) {
    return (
      <p className="notice" role="status">
        {t("sent")}
      </p>
    );
  }

  return (
    <form className="form" onSubmit={handleSubmit} noValidate>
      {failure !== undefined && <Alert>{translateError(failure)}</Alert>}
      <TextField id="reset-email" name="email" type="email" label={t("emailLabel")} placeholder={t("emailPlaceholder")} autoComplete="email" />
      <Button type="submit" disabled={submitting}>
        {submitting ? t("submitting") : t("submit")}
      </Button>
    </form>
  );
}
