import Link from "next/link";
import type { Metadata } from "next";
import { useTranslations } from "next-intl";
import { getTranslations } from "next-intl/server";
import { AuthShell } from "@/components/auth/AuthShell";
import { PasswordResetForm } from "@/components/auth/PasswordResetForm";
import { LOGIN_PATH } from "@/lib/auth/sign-in";

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations("Metadata.pages");
  return { title: t("passwordReset") };
}

export default function PasswordResetPage() {
  const t = useTranslations("Auth.passwordReset");

  return (
    <AuthShell
      title={t("title")}
      subtitle={t("subtitle")}
      footer={
        <Link href={LOGIN_PATH} className="link">
          {t("backToLogin")}
        </Link>
      }
    >
      <PasswordResetForm />
    </AuthShell>
  );
}
