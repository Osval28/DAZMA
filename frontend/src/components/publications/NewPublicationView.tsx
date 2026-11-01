"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect } from "react";
import { accountMessages } from "@/lib/account/messages";
import { ACCOUNT_PATH, HOME_PATH } from "@/lib/auth/sign-in";
import { publicationMessages } from "@/lib/publications/messages";
import { NEW_PUBLICATION_PATH } from "@/lib/publications/paths";
import { SiteHeader } from "@/components/account/SiteHeader";
import { useSessionProfile } from "@/components/account/useSessionProfile";
import { Alert } from "@/components/ui/Alert";
import { PublicationForm } from "./PublicationForm";

const messages = publicationMessages.create;

export function NewPublicationView() {
  const router = useRouter();
  const state = useSessionProfile(NEW_PUBLICATION_PATH);

  useEffect(() => {
    if (state.status === "missing") {
      router.replace(ACCOUNT_PATH);
    }
  }, [state.status, router]);

  return (
    <>
      <SiteHeader />
      <main className="publish">
        <Link href={HOME_PATH} className="detail__back">
          {messages.back}
        </Link>
        <section className="publish__card">
          <h1 className="publish__title">{messages.title}</h1>
          <p className="publish__subtitle">{messages.subtitle}</p>
          {(state.status === "loading" || state.status === "missing") && <p className="page__status">{accountMessages.loading}</p>}
          {state.status === "failed" && <Alert>{state.message}</Alert>}
          {state.status === "ready" && !state.profile.emailVerified && (
            <>
              <Alert>{messages.emailNotVerified}</Alert>
              <Link href={ACCOUNT_PATH} className="link">
                {messages.goToAccount}
              </Link>
            </>
          )}
          {state.status === "ready" && state.profile.emailVerified && <PublicationForm />}
        </section>
      </main>
    </>
  );
}
