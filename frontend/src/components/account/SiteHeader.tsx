"use client";

import Link from "next/link";
import { useTranslations } from "next-intl";
import { LOGIN_PATH, REGISTER_PATH } from "@/lib/auth/sign-in";
import { NEW_PUBLICATION_PATH } from "@/lib/publications/paths";
import { Wordmark } from "@/components/brand/Wordmark";
import { SearchBox } from "@/components/publications/SearchBox";
import { LocaleSwitcher } from "@/components/ui/LocaleSwitcher";
import { UserBadge } from "./UserBadge";
import { useSessionState } from "./useSessionState";

export function SiteHeader() {
  const t = useTranslations();
  const session = useSessionState();

  return (
    <header className="site-header">
      <div className="site-header__brand">
        <Wordmark />
      </div>
      <SearchBox />
      <nav className="site-header__actions">
        <LocaleSwitcher />
        {session.status === "member" && (
          <>
            <Link href={NEW_PUBLICATION_PATH} className="button button--accent site-header__cta">
              {t("Publications.create.headerAction")}
            </Link>
            <UserBadge name={session.name} />
          </>
        )}
        {session.status === "anonymous" && (
          <>
            <Link href={LOGIN_PATH} className="link">
              {t("Account.header.signIn")}
            </Link>
            <Link href={REGISTER_PATH} className="button button--primary site-header__cta">
              {t("Account.header.signUp")}
            </Link>
          </>
        )}
      </nav>
    </header>
  );
}
