import Link from "next/link";
import type { Metadata } from "next";
import { Suspense } from "react";
import { authMessages } from "@/lib/auth/messages";
import { AuthShell } from "@/components/auth/AuthShell";
import { LoginForm } from "@/components/auth/LoginForm";

const messages = authMessages.login;

export const metadata: Metadata = {
  title: "Ingresar | DAZMA",
};

export default function LoginPage() {
  return (
    <AuthShell
      title={messages.title}
      subtitle={messages.subtitle}
      footer={
        <>
          {messages.noAccount}{" "}
          <Link href="/registro" className="link">
            {messages.createAccount}
          </Link>
        </>
      }
    >
      <Suspense>
        <LoginForm />
      </Suspense>
    </AuthShell>
  );
}
