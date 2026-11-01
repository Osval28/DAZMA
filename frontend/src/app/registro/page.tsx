import Link from "next/link";
import type { Metadata } from "next";
import { useTranslations } from "next-intl";
import { getTranslations } from "next-intl/server";
import { AuthShell } from "@/components/auth/AuthShell";
import { RegisterForm } from "@/components/auth/RegisterForm";

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations("Metadata.pages");
  return { title: t("register") };
}

export default function RegisterPage() {
  const t = useTranslations("Auth.register");

  return (
    <AuthShell
      title={t("title")}
      subtitle={t("subtitle")}
      footer={
        <>
          {t("hasAccount")}{" "}
          <Link href="/ingresar" className="link">
            {t("signIn")}
          </Link>
        </>
      }
    >
      <RegisterForm />
    </AuthShell>
  );
}
