"use client";

import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { useState } from "react";
import type { FormEvent } from "react";
import { describeError, describeValidationIssue } from "@/lib/auth/errors";
import { authMessages } from "@/lib/auth/messages";
import { HOME_PATH, signInWithEmail } from "@/lib/auth/sign-in";
import { safeNextPath } from "@/lib/navigation/next-path";
import { validateCurrentPassword, validateEmail } from "@/lib/validation";
import { GoogleButton } from "@/components/auth/GoogleButton";
import { Alert } from "@/components/ui/Alert";
import { Button } from "@/components/ui/Button";
import { PasswordField } from "@/components/ui/PasswordField";
import { TextField } from "@/components/ui/TextField";

const messages = authMessages.login;

export function LoginForm() {
  const router = useRouter();
  const nextPath = safeNextPath(useSearchParams().get("next"));
  const [errorMessage, setErrorMessage] = useState<string>();
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const email = String(form.get("email")).trim();
    const password = String(form.get("password"));

    const issue = validateEmail(email) ?? validateCurrentPassword(password);
    if (issue !== undefined) {
      setErrorMessage(describeValidationIssue(issue));
      return;
    }

    setErrorMessage(undefined);
    setSubmitting(true);
    try {
      const destination = await signInWithEmail(email, password);
      router.replace(destination === HOME_PATH ? nextPath : destination);
    } catch (error) {
      setErrorMessage(describeError(error));
      setSubmitting(false);
    }
  }

  return (
    <form className="form" onSubmit={handleSubmit} noValidate>
      {errorMessage !== undefined && <Alert>{errorMessage}</Alert>}
      <TextField id="login-email" name="email" type="email" label={messages.emailLabel} placeholder={messages.emailPlaceholder} autoComplete="email" />
      <PasswordField id="login-password" name="password" label={messages.passwordLabel} placeholder={messages.passwordPlaceholder} autoComplete="current-password" />
      <Link href="/recuperar-contrasena" className="link form__aside-link">
        {messages.forgotPassword}
      </Link>
      <Button type="submit" disabled={submitting}>
        {submitting ? messages.submitting : messages.submit}
      </Button>
      <GoogleButton
        label={messages.googleButton}
        dividerLabel={messages.divider}
        disabled={submitting}
        onSignedIn={(destination) => router.replace(destination === HOME_PATH ? nextPath : destination)}
        onFailure={setErrorMessage}
      />
    </form>
  );
}
