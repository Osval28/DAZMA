"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useTranslations } from "next-intl";
import { useEffect } from "react";
import { ACCOUNT_PATH, HOME_PATH } from "@/lib/auth/sign-in";
import { NEW_PUBLICATION_PATH } from "@/lib/publications/paths";
import { SiteHeader } from "@/components/account/SiteHeader";
import { useSessionProfile } from "@/components/account/useSessionProfile";
import { VerifyEmailButton } from "@/components/account/VerifyEmailButton";
import { Alert } from "@/components/ui/Alert";
import { PublicationForm } from "./PublicationForm";

export function NewPublicationView() {
  const t = useTranslations();
  const router = useRouter();
  const { state, reload } = useSessionProfile(NEW_PUBLICATION_PATH);

  useEffect(() => {
    if (state.status === "missing") {
      router.replace(ACCOUNT_PATH);
    }
  }, [state.status, router]);

  return (
    <>
      <SiteHeader />
      <main id="contenido" className="publish">
        <Link href={HOME_PATH} className="detail__back">
          {t("Publications.create.back")}
        </Link>
        <section className="publish__card">
          <h1 className="publish__title">{t("Publications.create.title")}</h1>
          <p className="publish__subtitle">{t("Publications.create.subtitle")}</p>
          {(state.status === "loading" || state.status === "missing") && <p className="page__status">{t("Account.loading")}</p>}
          {state.status === "failed" && <Alert>{t(state.errorKey)}</Alert>}
          {state.status === "ready" && !state.profile.emailVerified && (
            <>
              <Alert>{t("Publications.create.emailNotVerified")}</Alert>
              <VerifyEmailButton returnPath={NEW_PUBLICATION_PATH} onRefreshed={reload} />
              <Link href={ACCOUNT_PATH} className="link">
                {t("Publications.create.goToAccount")}
              </Link>
            </>
          )}
          {state.status === "ready" && state.profile.emailVerified && <PublicationForm />}
        </section>
      </main>
    </>
  );
}
