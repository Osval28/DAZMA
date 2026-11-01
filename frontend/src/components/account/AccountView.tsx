"use client";

import Link from "next/link";
import { useTranslations } from "next-intl";
import { ACCOUNT_PATH, HOME_PATH } from "@/lib/auth/sign-in";
import { AuthShell } from "@/components/auth/AuthShell";
import { Alert } from "@/components/ui/Alert";
import { AccountProfile } from "./AccountProfile";
import { ProfileForm } from "./ProfileForm";
import { SiteHeader } from "./SiteHeader";
import { SignOutButton } from "./SignOutButton";
import { useSessionProfile } from "./useSessionProfile";

export function AccountView() {
  const t = useTranslations();
  const { state, reload } = useSessionProfile(ACCOUNT_PATH);

  if (state.status === "loading") {
    return (
      <main className="page">
        <p className="page__status">{t("Account.loading")}</p>
      </main>
    );
  }

  if (state.status === "failed") {
    return (
      <main className="page">
        <Alert>{t(state.errorKey)}</Alert>
      </main>
    );
  }

  if (state.status === "missing") {
    return (
      <AuthShell title={t("Account.completeProfile.title")} subtitle={t("Account.completeProfile.subtitle")} footer={<SignOutButton />}>
        <ProfileForm />
      </AuthShell>
    );
  }

  const { profile } = state;
  return (
    <>
      <SiteHeader />
      <main id="contenido" className="account">
        <Link href={HOME_PATH} className="detail__back">
          {t("Account.account.backHome")}
        </Link>
        <AccountProfile profile={profile} onReload={reload} />
      </main>
    </>
  );
}
