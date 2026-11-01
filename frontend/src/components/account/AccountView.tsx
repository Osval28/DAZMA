"use client";

import Link from "next/link";
import { accountMessages } from "@/lib/account/messages";
import { ACCOUNT_PATH, HOME_PATH } from "@/lib/auth/sign-in";
import { AuthShell } from "@/components/auth/AuthShell";
import { Alert } from "@/components/ui/Alert";
import { AccountProfile } from "./AccountProfile";
import { ProfileForm } from "./ProfileForm";
import { SiteHeader } from "./SiteHeader";
import { SignOutButton } from "./SignOutButton";
import { useSessionProfile } from "./useSessionProfile";

const messages = accountMessages.account;
const completeProfileMessages = accountMessages.completeProfile;

export function AccountView() {
  const state = useSessionProfile(ACCOUNT_PATH);

  if (state.status === "loading") {
    return (
      <main className="page">
        <p className="page__status">{accountMessages.loading}</p>
      </main>
    );
  }

  if (state.status === "failed") {
    return (
      <main className="page">
        <Alert>{state.message}</Alert>
      </main>
    );
  }

  if (state.status === "missing") {
    return (
      <AuthShell title={completeProfileMessages.title} subtitle={completeProfileMessages.subtitle} footer={<SignOutButton />}>
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
          {messages.backHome}
        </Link>
        <AccountProfile profile={profile} />
      </main>
    </>
  );
}
