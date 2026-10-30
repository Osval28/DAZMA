import Link from "next/link";
import type { Metadata } from "next";
import { authMessages } from "@/lib/auth/messages";
import { AuthShell } from "@/components/auth/AuthShell";
import { RegisterForm } from "@/components/auth/RegisterForm";

const messages = authMessages.register;

export const metadata: Metadata = {
  title: "Crear cuenta | DAZMA",
};

export default function RegisterPage() {
  return (
    <AuthShell
      title={messages.title}
      subtitle={messages.subtitle}
      footer={
        <>
          {messages.hasAccount}{" "}
          <Link href="/ingresar" className="link">
            {messages.signIn}
          </Link>
        </>
      }
    >
      <RegisterForm />
    </AuthShell>
  );
}
