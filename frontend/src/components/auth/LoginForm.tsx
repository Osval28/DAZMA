"use client";

import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { useTranslations } from "next-intl";
import { useState } from "react";
import type { FormEvent } from "react";
import type { AuthErrorKey } from "@/i18n/message-keys";
import { errorKey, validationIssueKey } from "@/lib/auth/errors";
import { PASSWORD_RESET_PATH } from "@/lib/auth/password-reset";
import { HOME_PATH, signInWithEmail } from "@/lib/auth/sign-in";
import { safeNextPath } from "@/lib/navigation/next-path";
import { validateCurrentPassword, validateEmail } from "@/lib/validation";
import { GoogleButton } from "@/components/auth/GoogleButton";
import { Alert } from "@/components/ui/Alert";
import { Button } from "@/components/ui/Button";
import { PasswordField } from "@/components/ui/PasswordField";
import { TextField } from "@/components/ui/TextField";

export function LoginForm() {
  const t = useTranslations();
  const router = useRouter();
  const nextPath = safeNextPath(useSearchParams().get("next"));
  const [failure, setFailure] = useState<AuthErrorKey>();
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const email = String(form.get("email")).trim();
    const password = String(form.get("password"));

    const issue = validateEmail(email) ?? validateCurrentPassword(password);
    if (issue !== undefined) {
      setFailure(validationIssueKey(issue));
      return;
    }

    setFailure(undefined);
    setSubmitting(true);
    try {
      const destination = await signInWithEmail(email, password);
      router.replace(destination === HOME_PATH ? nextPath : destination);
    } catch (error) {
      setFailure(errorKey(error));
      setSubmitting(false);
    }
  }

  return (
    <form className="form" onSubmit={handleSubmit} noValidate>
      {failure !== undefined && <Alert>{t(failure)}</Alert>}
      <TextField id="login-email" name="email" type="email" label={t("Auth.login.emailLabel")} placeholder={t("Auth.login.emailPlaceholder")} autoComplete="email" />
      <PasswordField id="login-password" name="password" label={t("Auth.login.passwordLabel")} placeholder={t("Auth.login.passwordPlaceholder")} autoComplete="current-password" />
      <Link href={PASSWORD_RESET_PATH} className="link form__aside-link">
        {t("Auth.login.forgotPassword")}
      </Link>
      <Button type="submit" disabled={submitting}>
        {submitting ? t("Auth.login.submitting") : t("Auth.login.submit")}
      </Button>
      <GoogleButton
        label={t("Auth.login.googleButton")}
        dividerLabel={t("Auth.login.divider")}
        disabled={submitting}
        onSignedIn={(destination) => router.replace(destination === HOME_PATH ? nextPath : destination)}
        onFailure={setFailure}
      />
    </form>
  );
}
