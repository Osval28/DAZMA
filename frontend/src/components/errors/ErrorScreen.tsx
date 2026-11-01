"use client";

import Link from "next/link";
import { useTranslations } from "next-intl";
import { HOME_PATH } from "@/lib/auth/sign-in";
import { SiteHeader } from "@/components/account/SiteHeader";
import { Button } from "@/components/ui/Button";
import { CloudOffIcon } from "@/components/ui/icons";

type ErrorScreenProps = {
  title: string;
  text: string;
  onRetry: () => void;
};

export function ErrorScreen({ title, text, onRetry }: ErrorScreenProps) {
  const t = useTranslations("Errors");

  return (
    <>
      <SiteHeader />
      <main id="contenido" className="error-state">
        <div className="error-state__icon">
          <CloudOffIcon />
        </div>
        <h1 className="error-state__title">{title}</h1>
        <p className="error-state__text">{text}</p>
        <div className="error-state__actions">
          <Button onClick={onRetry}>{t("retry")}</Button>
          <Link href={HOME_PATH} className="button button--secondary">
            {t("home")}
          </Link>
        </div>
      </main>
    </>
  );
}
