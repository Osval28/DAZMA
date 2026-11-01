"use client";

import { useRouter } from "next/navigation";
import { useTranslations } from "next-intl";
import { useState } from "react";
import type { FormEvent } from "react";
import type { AuthErrorKey } from "@/i18n/message-keys";
import { registerUser } from "@/lib/api/users";
import { errorKey, validationIssueKey } from "@/lib/auth/errors";
import { readProfileInput } from "@/lib/auth/profile-input";
import { ACCOUNT_PATH, HOME_PATH, createAccount } from "@/lib/auth/sign-in";
import { validateEmail, validateNewPassword, validateProfile } from "@/lib/validation";
import { GoogleButton } from "@/components/auth/GoogleButton";
import { Alert } from "@/components/ui/Alert";
import { Button } from "@/components/ui/Button";
import { PasswordField } from "@/components/ui/PasswordField";
import { ProfileFields } from "@/components/ui/ProfileFields";
import { TextField } from "@/components/ui/TextField";

export function RegisterForm() {
  const t = useTranslations();
  const router = useRouter();
  const [failure, setFailure] = useState<AuthErrorKey>();
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const email = String(form.get("email")).trim();
    const password = String(form.get("password"));
    const confirmation = String(form.get("confirmPassword"));
    const profile = readProfileInput(form);

    const issue = validateEmail(email) ?? validateNewPassword(password, confirmation) ?? validateProfile(profile);
    if (issue !== undefined) {
      setFailure(validationIssueKey(issue));
      return;
    }

    setFailure(undefined);
    setSubmitting(true);
    try {
      await createAccount(email, password);
    } catch (error) {
      setFailure(errorKey(error));
      setSubmitting(false);
      return;
    }

    try {
      await registerUser(profile);
      router.replace(HOME_PATH);
    } catch {
      router.replace(ACCOUNT_PATH);
    }
  }

  return (
    <form className="form" onSubmit={handleSubmit} noValidate>
      {failure !== undefined && <Alert>{t(failure)}</Alert>}
      <TextField id="register-email" name="email" type="email" label={t("Auth.register.emailLabel")} placeholder={t("Auth.register.emailPlaceholder")} autoComplete="email" />
      <PasswordField id="register-password" name="password" label={t("Auth.register.passwordLabel")} placeholder={t("Auth.register.passwordPlaceholder")} autoComplete="new-password" />
      <PasswordField id="register-confirm-password" name="confirmPassword" label={t("Auth.register.confirmPasswordLabel")} placeholder={t("Auth.register.confirmPasswordPlaceholder")} autoComplete="new-password" />
      <ProfileFields idPrefix="register" />
      <Button type="submit" disabled={submitting}>
        {submitting ? t("Auth.register.submitting") : t("Auth.register.submit")}
      </Button>
      <GoogleButton
        label={t("Auth.register.googleButton")}
        dividerLabel={t("Auth.register.divider")}
        disabled={submitting}
        onSignedIn={(destination) => router.replace(destination)}
        onFailure={setFailure}
      />
    </form>
  );
}
