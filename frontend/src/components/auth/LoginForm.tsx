"use client";

import Link from "next/link";
import type { FormEvent } from "react";
import { authMessages } from "@/lib/auth/messages";
import { Button } from "@/components/ui/Button";
import { GoogleIcon } from "@/components/ui/icons";
import { PasswordField } from "@/components/ui/PasswordField";
import { TextField } from "@/components/ui/TextField";

const messages = authMessages.login;

export function LoginForm() {
  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
  }

  return (
    <>
      <form className="form" onSubmit={handleSubmit} noValidate>
        <TextField id="login-email" name="email" type="email" label={messages.emailLabel} placeholder={messages.emailPlaceholder} autoComplete="email" />
        <PasswordField id="login-password" name="password" label={messages.passwordLabel} placeholder={messages.passwordPlaceholder} autoComplete="current-password" />
        <Link href="/recuperar-contrasena" className="link form__aside-link">
          {messages.forgotPassword}
        </Link>
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
