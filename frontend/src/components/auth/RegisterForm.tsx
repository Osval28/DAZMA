"use client";

import type { FormEvent } from "react";
import { authMessages } from "@/lib/auth/messages";
import { Button } from "@/components/ui/Button";
import { GoogleIcon } from "@/components/ui/icons";
import { PasswordField } from "@/components/ui/PasswordField";
import { TextField } from "@/components/ui/TextField";

const messages = authMessages.register;

export function RegisterForm() {
  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
  }

  return (
    <>
      <form className="form" onSubmit={handleSubmit} noValidate>
        <TextField id="register-email" name="email" type="email" label={messages.emailLabel} placeholder={messages.emailPlaceholder} autoComplete="email" />
        <PasswordField id="register-password" name="password" label={messages.passwordLabel} placeholder={messages.passwordPlaceholder} autoComplete="new-password" />
        <PasswordField id="register-confirm-password" name="confirmPassword" label={messages.confirmPasswordLabel} placeholder={messages.confirmPasswordPlaceholder} autoComplete="new-password" />
        <Button type="submit">{messages.submit}</Button>
      </form>
      <div className="divider">{messages.divider}</div>
      <Button variant="secondary">
        <GoogleIcon />
        {messages.googleButton}
      </Button>
    </>
  );
}
