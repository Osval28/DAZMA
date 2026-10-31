"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import type { FormEvent } from "react";
import { registerUser } from "@/lib/api/users";
import { describeError, describeValidationIssue } from "@/lib/auth/errors";
import { authMessages } from "@/lib/auth/messages";
import { readProfileInput } from "@/lib/auth/profile-input";
import { ACCOUNT_PATH, HOME_PATH, createAccount } from "@/lib/auth/sign-in";
import { validateEmail, validateNewPassword, validateProfile } from "@/lib/validation";
import { GoogleButton } from "@/components/auth/GoogleButton";
import { Alert } from "@/components/ui/Alert";
import { Button } from "@/components/ui/Button";
import { PasswordField } from "@/components/ui/PasswordField";
import { ProfileFields } from "@/components/ui/ProfileFields";
import { TextField } from "@/components/ui/TextField";

const messages = authMessages.register;

export function RegisterForm() {
  const router = useRouter();
  const [errorMessage, setErrorMessage] = useState<string>();
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
      setErrorMessage(describeValidationIssue(issue));
      return;
    }

    setErrorMessage(undefined);
    setSubmitting(true);
    try {
      await createAccount(email, password);
    } catch (error) {
      setErrorMessage(describeError(error));
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
      {errorMessage !== undefined && <Alert>{errorMessage}</Alert>}
      <TextField id="register-email" name="email" type="email" label={messages.emailLabel} placeholder={messages.emailPlaceholder} autoComplete="email" />
      <PasswordField id="register-password" name="password" label={messages.passwordLabel} placeholder={messages.passwordPlaceholder} autoComplete="new-password" />
      <PasswordField id="register-confirm-password" name="confirmPassword" label={messages.confirmPasswordLabel} placeholder={messages.confirmPasswordPlaceholder} autoComplete="new-password" />
      <ProfileFields idPrefix="register" />
      <Button type="submit" disabled={submitting}>
        {submitting ? messages.submitting : messages.submit}
      </Button>
      <GoogleButton
        label={messages.googleButton}
        dividerLabel={messages.divider}
        disabled={submitting}
        onSignedIn={(destination) => router.replace(destination)}
        onFailure={setErrorMessage}
      />
    </form>
  );
}
